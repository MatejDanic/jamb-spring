package matej.api.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import matej.api.repositories.BoxRepository;
import matej.api.repositories.ColumnRepository;
import matej.api.repositories.DiceRepository;
import matej.api.repositories.FormRepository;
import matej.api.repositories.ScoreRepository;
import matej.api.repositories.UserRepository;
import matej.constants.JambConstants;
import matej.exceptions.IllegalMoveException;
import matej.exceptions.InvalidOwnershipException;
import matej.factories.JambFactory;
import matej.models.Box;
import matej.models.Dice;
import matej.models.Form;
import matej.models.Column;
import matej.models.Score;
import matej.models.User;
import matej.models.enums.BoxType;
import matej.models.enums.ColumnType;

/**
 * Service Class for managing {@link Form} repostiory
 *
 * @author MatejDanic
 * @version 1.0
 * @since 2020-08-20
 */
@Service
public class FormService {

	@Autowired
	FormRepository formRepo;

	@Autowired
	ScoreRepository scoreRepo;

	@Autowired
	ColumnRepository columnRepo;

	@Autowired
	BoxRepository boxRepo;

	@Autowired
	DiceRepository diceRepo;

	@Autowired
	UserRepository userRepo;

	/**
	 * Creates or retrieves existing {@link Form} Object for the current {@link User}.
	 * 
	 * @param username      the username of the current user
	 * 
	 * @return {@link Form} the form that the game will be played on
	 * 
	 * @throws UsernameNotFoundException if user with given username does not exist
	 */
	public Form initializeForm(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		if (user.getForm() != null) {
			return formRepo.findById(user.getForm().getId()).get();
		} else {
			Form form = JambFactory.createForm(user);
			System.out.println(form);
			return formRepo.save(form);
		}
	}

	/**
	 * Deletes {@link Form} object from database repository.
	 * 
	 * @param username the username of the form owner
	 * 
	 * @return {@link Form} the form that the game will be played on
	 * 
	 * @throws UsernameNotFoundException if user with given username does not exist
	 */
	public void deleteFormById(String username, int id) throws InvalidOwnershipException {
		formRepo.deleteById(id);
	}

	public void deleteFormSaveScore(Form form, User user, int finalSum) {
		Score score = JambFactory.createScore(user, finalSum);
		scoreRepo.save(score);
		formRepo.delete(form);
	}

	public Form getFormById(int id) {
		return formRepo.findById(id).get();
	}

	public Column getColumn(int id, int columnTypeOrdinal) {
		return getFormById(id).getColumnByType(ColumnType.fromOrdinal(columnTypeOrdinal));
	}

	public Box getColumnBox(int id, int columnTypeOrdinal, int boxTypeOrdinal) {
		return getFormById(id).getColumnByType(ColumnType.fromOrdinal(columnTypeOrdinal))
				.getBoxByType(BoxType.fromOrdinal(boxTypeOrdinal));
	}

	public List<Form> getFormList() {
		return formRepo.findAll();
	}

	public List<Dice> rollDice(String username, int id, Map<Integer, Boolean> diceToThrow)
			throws IllegalMoveException, InvalidOwnershipException {
		if (!checkOwnership(username, id))
			throw new InvalidOwnershipException("Form with id " + id + " doesn't belong to user " + username);
		Form form = getFormById(id);

		if (form.getRollCount() == 0)
			diceToThrow.replaceAll((k, v) -> v = true);
		else if (form.getRollCount() == JambConstants.NUM_OF_ROLLS)
			throw new IllegalMoveException("Dice roll limit reached!");
		else if (form.getRollCount() > 0 && form.isAnnouncementRequired() && form.getAnnouncement() == null)
			throw new IllegalMoveException("Announcement is required!");

		if (form.getRollCount() < JambConstants.NUM_OF_ROLLS) {
			form.setRollCount(form.getRollCount() + 1);
			
		}
		List<Dice> dice = form.getDice();
		for (Map.Entry<Integer, Boolean> entry : diceToThrow.entrySet()) {
			Dice d = form.getDiceByOrdinalNumber(entry.getKey());
			if (entry.getValue()) {
				d.setForm(form);
				d.setOrdinalNumber(entry.getKey());
				d.roll();
				dice.add(d);
			}
		}
		form.setDice(dice);
		formRepo.save(form);
		return form.getDice();
	}

	public int announce(String username, int id, int announcementOrdinal)
			throws IllegalMoveException, InvalidOwnershipException {
		if (!checkOwnership(username, id))
			throw new InvalidOwnershipException("Form with id " + id + " doesn't belong to user " + username);

		Form form = getFormById(id);

		if (form.getAnnouncement() != null)
			throw new IllegalMoveException("Announcement already declared!");
		if (form.getRollCount() >= 2)
			throw new IllegalMoveException("Announcement unavailable after second roll!");

		form.setAnnouncement(announcementOrdinal);
		formRepo.save(form);
		return announcementOrdinal;
	}

	public int fillBox(String username, int id, int columnTypeOrdinal, int boxTypeOrdinal)
			throws IllegalMoveException, InvalidOwnershipException {
		if (!checkOwnership(username, id))
			throw new InvalidOwnershipException("Form with id " + id + " doesn't belong to user " + username);

		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		Form form = getFormById(id);
		Column column = form.getColumnByType(ColumnType.fromOrdinal(columnTypeOrdinal));
		Box box = column.getBoxByType(BoxType.fromOrdinal(boxTypeOrdinal));

		if (box.isFilled())
			throw new IllegalMoveException("Box already filled!");
		else if (form.getRollCount() == 0)
			throw new IllegalMoveException("Cannot fill box without rolling dice!");
		else if (!box.isAvailable() && BoxType.fromOrdinal(form.getAnnouncement()) == null)
			throw new IllegalMoveException("Box is currently not available!");
		else if (form.getAnnouncement() != null && BoxType.fromOrdinal(form.getAnnouncement()) != box.getBoxType())
			throw new IllegalMoveException("Box is not the same as announcement!");

		box.fill(form.getDice());
		box.setColumn(column);

		advanceColumn(form, columnTypeOrdinal, boxTypeOrdinal);
		column.setForm(form);

		if (isFormCompleted(form)) {
			deleteFormSaveScore(form, user, form.calculateFinalSum());
		} else {
			form.setRollCount(0);
			form.setAnnouncement(null);
		}
		formRepo.save(form);
		return box.getValue();
	}

	public Map<String, Integer> getSums(String username, int id) throws InvalidOwnershipException {
		if (!checkOwnership(username, id))
			throw new InvalidOwnershipException("Form with id " + id + " doesn't belong to user " + username);
		Form form = getFormById(id);
		return form.calculateSums();
	}

	private boolean isFormCompleted(Form form) {
		for (Column column : form.getColumns()) {
			if (!column.isCompleted())
				return false;
		}
		return true;
	}

	public void advanceColumn(Form form, int columnTypeOrdinal, int boxTypeOrdinal) {
		Box nextBox = new Box();
		Column column = form.getColumnByType(ColumnType.fromOrdinal(columnTypeOrdinal));
		if (column.getColumnType() == ColumnType.DOWNWARDS) {
			boxTypeOrdinal++;
		} else if (column.getColumnType() == ColumnType.UPWARDS) {
			boxTypeOrdinal--;
		} else {
			return;
		}
		try {
			nextBox = column.getBoxByType(BoxType.fromOrdinal(boxTypeOrdinal));
			nextBox.setAvailable(true);
			nextBox.setColumn(column);
			boxRepo.save(nextBox);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}
	}

	private boolean checkOwnership(String username, int formId) {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		return user.getForm().getId() == formId;
	}

}
