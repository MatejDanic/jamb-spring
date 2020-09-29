package matej.api.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import matej.api.repositories.BoxRepository;
import matej.api.repositories.BoxTypeRepository;
import matej.api.repositories.ColumnRepository;
import matej.api.repositories.ColumnTypeRepository;
import matej.api.repositories.DiceRepository;
import matej.api.repositories.FormRepository;
import matej.api.repositories.ScoreRepository;
import matej.api.repositories.UserRepository;
import matej.constants.GameConstants;
import matej.exceptions.IllegalMoveException;
import matej.exceptions.InvalidOwnershipException;
import matej.factories.GameFactory;
import matej.models.GameBox;
import matej.models.GameDice;
import matej.models.GameForm;
import matej.models.GameColumn;
import matej.models.GameScore;
import matej.models.AuthUser;

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
	ColumnTypeRepository columnTypeRepo;

	@Autowired
	BoxRepository boxRepo;

	@Autowired
	BoxTypeRepository boxTypeRepo;

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
	public GameForm initializeForm(String username) throws UsernameNotFoundException {
		AuthUser user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		if (user.getForm() != null) {
			return formRepo.findById(user.getForm().getId()).get();
		} else {
			GameForm form = GameFactory.createForm(user, columnTypeRepo.findAll(), boxTypeRepo.findAll());
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

	public void deleteFormSaveScore(GameForm form, AuthUser user, int finalSum) {
		GameScore score = GameFactory.createScore(user, finalSum);
		scoreRepo.save(score);
		formRepo.delete(form);
	}

	public GameForm getFormById(int id) {
		return formRepo.findById(id).get();
	}

	public GameColumn getColumn(int id, int columnTypeOrdinal) {
		return getFormById(id).getColumnByTypeId(columnTypeOrdinal);
	}

	public GameBox getColumnBox(int id, int columnTypeOrdinal, int boxTypeOrdinal) {
		return getFormById(id).getColumnByTypeId(columnTypeOrdinal)
				.getBoxByTypeId(boxTypeOrdinal);
	}

	public List<GameForm> getFormList() {
		return formRepo.findAll();
	}

	public List<GameDice> rollDice(String username, int id, Map<Integer, Boolean> diceToThrow)
			throws IllegalMoveException, InvalidOwnershipException {
		if (!checkOwnership(username, id))
			throw new InvalidOwnershipException("Form with id " + id + " doesn't belong to user " + username);
		GameForm form = getFormById(id);

		if (form.getRollCount() == 0)
			diceToThrow.replaceAll((k, v) -> v = true);
		else if (form.getRollCount() == GameConstants.NUM_OF_ROLLS)
			throw new IllegalMoveException("Dice roll limit reached!");
		else if (form.getRollCount() > 0 && form.checkAnnouncementRequired() && form.getAnnouncement() == null)
			throw new IllegalMoveException("Announcement is required!");

		if (form.getRollCount() < GameConstants.NUM_OF_ROLLS) {
			form.setRollCount(form.getRollCount() + 1);
			formRepo.save(form);
		}
		List<GameDice> dice = form.getDice();
		for (Map.Entry<Integer, Boolean> entry : diceToThrow.entrySet()) {
			for (GameDice d : dice) {
				if (entry.getKey() == d.getOrdinalNumber()) {
					if (entry.getValue()) d.roll();
					break;
				}
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

		GameForm form = getFormById(id);

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

		AuthUser user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		GameForm form = getFormById(id);
		GameColumn column = form.getColumnByTypeId(columnTypeOrdinal);
		GameBox box = column.getBoxByTypeId(boxTypeOrdinal);

		if (box.isFilled())
			throw new IllegalMoveException("Box already filled!");
		else if (form.getRollCount() == 0)
			throw new IllegalMoveException("Cannot fill box without rolling dice!");
		else if (!box.isAvailable() && boxTypeRepo.findById(form.getAnnouncement()).get() == null)
			throw new IllegalMoveException("Box is currently not available!");
		else if (form.getAnnouncement() != null && boxTypeRepo.findById(form.getAnnouncement()).get().getLabel() != box.getBoxType().getLabel())
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
		GameForm form = getFormById(id);
		return form.calculateSums();
	}

	private boolean isFormCompleted(GameForm form) {
		for (GameColumn column : form.getColumns()) {
			if (!column.isCompleted())
				return false;
		}
		return true;
	}

	public void advanceColumn(GameForm form, int columnTypeOrdinal, int boxTypeOrdinal) {
		GameColumn column = form.getColumnByTypeId(columnTypeOrdinal);
		if (column.getColumnType().getLabel() == "DOWNWARDS") {
			boxTypeOrdinal++;
		} else if (column.getColumnType().getLabel() == "UPWARDS") {
			boxTypeOrdinal--;
		} else {
			return;
		}
		try {
			GameBox nextBox = column.getBoxByTypeId(boxTypeOrdinal);
			nextBox.setAvailable(true);
			formRepo.save(form);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}
	}

	private boolean checkOwnership(String username, int formId) {
		AuthUser user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		return user.getForm().getId() == formId;
	}

}
