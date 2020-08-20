package matej.factories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import matej.constants.JambConstants;
import matej.models.Box;
import matej.models.Dice;
import matej.models.Form;
import matej.models.Column;
import matej.models.Score;
import matej.models.User;
import matej.models.enums.BoxType;
import matej.models.enums.ColumnType;

/*
 * This factory class contains methods for instantiating various objects of Jamb.
 *
 * @author MatejDanic
 * @version 1.0
 * @since 2020-08-16
 */
public class JambFactory {

	/**
	 * This method starts the initialization process by creating the game form
	 * 
	 * @param user the owner of the form
	 * 
	 * @return {@link Form} the created form object
	 */
	public static Form createForm(User user) {
		Form form = new Form();
		form.setColumns(createColumns(form)); // create columns of the form
		for (Column column : form.getColumns()) {
			column.setBoxes(createBoxes(column)); // for each column create its boxes
		}
		form.setDice(createDice(form)); // create dice to roll for the form
		form.setRollCount(0);
		form.setAnnouncement(null);
		form.setUser(user);
		return form;
	}

	public static Score createScore(User user, int finalSum) {
		Score score = new Score();
		score.setUser(user);
		// score.setUsername(user.getUsername());
		score.setValue(finalSum);
		score.setDate(LocalDate.now());
		return score;
	}

	public static List<Column> createColumns(Form form) {
		List<Column> columns = new ArrayList<>();
		for (ColumnType columnType : ColumnType.values()) {
			Column column = new Column();
			column.setForm(form);
			column.setColumnType(columnType);
			columns.add(column);
		}
		return columns;
	}

	public static List<Box> createBoxes(Column column) {
		List<Box> boxes = new ArrayList<>();
		for (BoxType boxType : BoxType.values()) {
			Box box = new Box();
			box.setColumn(column);
			box.setBoxType(boxType);
			if (column.getColumnType() == ColumnType.ANY_DIRECTION || column.getColumnType() == ColumnType.ANNOUNCEMENT)
				box.setAvailable(true);
			else if (column.getColumnType() == ColumnType.DOWNWARDS && boxType == BoxType.ONES)
				box.setAvailable(true);
			else if (column.getColumnType() == ColumnType.UPWARDS && boxType == BoxType.JAMB)
				box.setAvailable(true);
			boxes.add(box);
		}
		return boxes;
	}

	public static List<Dice> createDice(Form form) {
		List<Dice> diceList = new ArrayList<>();
		for (int i = 0; i < JambConstants.NUM_OF_DICE; i++) {
			Dice dice = new Dice();
			dice.setForm(form);
			dice.setValue(6);
			dice.setLabel(i);
			diceList.add(dice);
		}
		return diceList;
	}

}
