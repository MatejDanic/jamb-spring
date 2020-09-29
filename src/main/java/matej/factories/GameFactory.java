package matej.factories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import matej.constants.GameConstants;
import matej.models.GameBox;
import matej.models.GameDice;
import matej.models.GameForm;
import matej.models.GameColumn;
import matej.models.GameScore;
import matej.models.AuthUser;
import matej.models.types.BoxType;
import matej.models.types.ColumnType;

/*
 * This factory class contains methods for instantiating various objects of Jamb.
 *
 * @author MatejDanic
 * @version 1.0
 * @since 2020-08-16
 */
public class GameFactory {

	/**
	 * This method starts the initialization process by creating the game form
	 * 
	 * @param user the owner of the form
	 * 
	 * @return {@link GameForm} the created form object
	 */
	public static GameForm createForm(AuthUser user, List<ColumnType> columnTypes, List<BoxType> boxTypes) {
		GameForm form = new GameForm();
		form.setColumns(createColumns(form, columnTypes)); // create columns of the form
		for (GameColumn column : form.getColumns()) {
			column.setBoxes(createBoxes(column, boxTypes)); // for each column create its boxes
		}
		form.setDice(createDice(form)); // create dice to roll for the form
		form.setRollCount(0);
		form.setAnnouncement(null);
		form.setUser(user);
		return form;
	}

	public static GameScore createScore(AuthUser user, int finalSum) {
		GameScore score = new GameScore();
		score.setUser(user);
		score.setValue(finalSum);
		score.setDate(LocalDateTime.now());
		return score;
	}

	public static List<GameColumn> createColumns(GameForm form, List<ColumnType> columnTypes) {
		List<GameColumn> columns = new ArrayList<>();
		for (ColumnType columnType : columnTypes) {
			GameColumn column = new GameColumn();
			column.setForm(form);
			column.setColumnType(columnType);
			columns.add(column);
		}
		return columns;
	}

	public static List<GameBox> createBoxes(GameColumn column, List<BoxType> boxTypes) {
		List<GameBox> boxes = new ArrayList<>();
		for (BoxType boxType : boxTypes) {
			GameBox box = new GameBox();
			box.setColumn(column);
			box.setBoxType(boxType);
			if (column.getColumnType().getLabel() == "ANY_DIRECTION" || column.getColumnType().getLabel() == "ANNOUNCEMENT")
				box.setAvailable(true);
			else if (column.getColumnType().getLabel() == "DOWNWARDS" && boxType.getLabel() == "ONES")
				box.setAvailable(true);
			else if (column.getColumnType().getLabel() == "UPWARDS" && boxType.getLabel() == "JAMB")
				box.setAvailable(true);
			boxes.add(box);
		}
		return boxes;
	}

	public static List<GameDice> createDice(GameForm form) {
		List<GameDice> diceList = new ArrayList<>();
		for (int i = 0; i < GameConstants.NUM_OF_DICE; i++) {
			GameDice dice = new GameDice();
			dice.setForm(form);
			dice.setValue(6);
			dice.setOrdinalNumber(i);
			diceList.add(dice);
		}
		return diceList;
	}

}
