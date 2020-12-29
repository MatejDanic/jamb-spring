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
import matej.models.types.BoxType;
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
	 * Creates or retrieves existing {@link Form} Object for the current
	 * {@link User}.
	 * 
	 * @param username the username of the current user
	 * 
	 * @return {@link Form} the form that the game will be played on
	 * 
	 * @throws UsernameNotFoundException if user with given username does not exist
	 */
	public GameForm initializeForm(String username) throws UsernameNotFoundException {
		AuthUser user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Korisnik s imenom " + username + " nije pronađen."));
		if (user.getForm() != null) {
			return formRepo.findById(user.getForm().getId()).get();
		} else {
			GameForm form = GameFactory.createForm(user, columnTypeRepo.findAll(), boxTypeRepo.findAll());
			return formRepo.save(form);
		}
	}

	public GameForm newForm(String username) throws UsernameNotFoundException, InvalidOwnershipException {
		AuthUser user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Korisnik s imenom " + username + " nije pronađen."));
		if (user.getForm() != null) {
			restartFormById(user.getForm().getId());
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
	 * @param id       the id of the form
	 * 
	 * @throws InvalidOwnershipException if form does not belong to user
	 */
	public void deleteFormById(int id) throws InvalidOwnershipException {
		formRepo.deleteById(id);
	}

	/**
	 * Deletes {@link Form} object from database repository.
	 * 
	 * @param username the username of the form owner
	 * @param id       the id of the form
	 * 
	 * @throws InvalidOwnershipException if form does not belong to user
	 */
	public void restartFormById(int id) throws InvalidOwnershipException {
		GameForm form = getFormById(id);
		form.setAnnouncement(null);
		form.setRollCount(0);
		for (GameColumn column : form.getColumns()) {
			for (GameBox box : column.getBoxes()) {
				box.setValue(0);
				if (column.getColumnType().getLabel().equals("ANY_DIRECTION")
						|| column.getColumnType().getLabel().equals("ANNOUNCEMENT"))
					box.setAvailable(true);
				else if (column.getColumnType().getLabel().equals("DOWNWARDS")
						&& box.getBoxType().getLabel().equals("ONES"))
					box.setAvailable(true);
				else if (column.getColumnType().getLabel().equals("UPWARDS")
						&& box.getBoxType().getLabel().equals("JAMB"))
					box.setAvailable(true);
				else
					box.setAvailable(false);
				box.setFilled(false);
			}
		}
		for (GameDice dice : form.getDice()) {
			dice.setValue(6);
		}
		formRepo.save(form);
	}

	public void saveScore(GameForm form) {
		GameScore score = GameFactory.createScore(form.getUser(), form.calculateFinalSum());
		scoreRepo.save(score);
	}

	public GameForm getFormById(int id) {
		return formRepo.findById(id).get();
	}

	public GameColumn getColumn(int id, int columnTypeId) {
		return getFormById(id).getColumnByTypeId(columnTypeId);
	}

	public GameBox getColumnBox(int id, int columnTypeId, int boxTypeId) {
		return getFormById(id).getColumnByTypeId(columnTypeId).getBoxByTypeId(boxTypeId);
	}

	public List<GameForm> getFormList() {
		return formRepo.findAll();
	}

	public List<GameDice> rollDice(int id, Map<Integer, Boolean> diceToThrow)
			throws IllegalMoveException, InvalidOwnershipException {

		GameForm form = getFormById(id);
		if (form.getRollCount() == 0)
			diceToThrow.replaceAll((k, v) -> v = true);
		else if (form.getRollCount() == GameConstants.NUM_OF_ROLLS)
			throw new IllegalMoveException("Kocka je bačena maksimalni broj puta!");
		else if (form.getRollCount() == 1 && form.isAnnouncementRequired() && form.getAnnouncement() == null)
			throw new IllegalMoveException("Najava je obavezna!");

		if (form.getRollCount() < GameConstants.NUM_OF_ROLLS) {
			form.setRollCount(form.getRollCount() + 1);
			formRepo.save(form);
		}
		List<GameDice> dice = form.getDice();
		for (Map.Entry<Integer, Boolean> entry : diceToThrow.entrySet()) {
			for (GameDice d : dice) {
				if (entry.getKey() == d.getOrdinalNumber()) {
					if (entry.getValue())
						d.roll();
					break;
				}
			}
		}
		form.setDice(dice);
		formRepo.save(form);
		return form.getDice();
	}

	public BoxType announce(int formId, BoxType boxType) throws IllegalMoveException, InvalidOwnershipException {

		GameForm form = getFormById(formId);
		if (form.getAnnouncement() != null)
			throw new IllegalMoveException("Najava je već iskorištena!");
		if (form.getRollCount() >= 2)
			throw new IllegalMoveException("Najava nije dostupna nakon drugog bacanja!");

		form.setAnnouncement(boxType);
		formRepo.save(form);
		return boxType;
	}

	public int fillBox(int id, int columnTypeId, int boxTypeId) throws IllegalMoveException, InvalidOwnershipException {

		GameForm form = getFormById(id);
		GameColumn column = form.getColumnByTypeId(columnTypeId);
		GameBox box = column.getBoxByTypeId(boxTypeId);

		if (box.isFilled())
			throw new IllegalMoveException("Kućica već popunjena!");
		else if (!box.isAvailable())
			throw new IllegalMoveException("Kućica nije dostupna!");
		else if (form.getRollCount() == 0)
			throw new IllegalMoveException("Ne može se upisati u kućicu bez bacanja kocki!");
		else if (form.getAnnouncement() != null && form.getAnnouncement().getId() != box.getBoxType().getId())
			throw new IllegalMoveException("Kućica nije jednaka najavi!");

		box.fill(form.getDice());
		box.setColumn(column);

		advanceColumn(form, columnTypeId, boxTypeId);
		column.setForm(form);

		if (form.isCompleted()) {
			saveScore(form);
		} else {
			form.setRollCount(0);
			form.setAnnouncement(null);
		}
		formRepo.save(form);
		return box.getValue();
	}

	public void advanceColumn(GameForm form, int columnTypeId, int boxTypeId) {
		GameColumn column = form.getColumnByTypeId(columnTypeId);
		if (column.getColumnType().getLabel().equals("DOWNWARDS")) {
			boxTypeId++;
		} else if (column.getColumnType().getLabel().equals("UPWARDS")) {
			boxTypeId--;
		} else {
			return;
		}
		try {
			GameBox nextBox = column.getBoxByTypeId(boxTypeId);
			nextBox.setAvailable(true);
			formRepo.save(form);
		} catch (IndexOutOfBoundsException exc) {
			System.out.println(exc.getMessage());
		}
	}
}
