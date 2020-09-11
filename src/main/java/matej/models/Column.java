package matej.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import matej.constants.JambConstants;
import matej.models.composite.ColumnId;
import matej.models.enums.BoxType;
import matej.models.enums.ColumnType;

@Entity
@Table(name = "\"column\"")
@IdClass(ColumnId.class)
public class Column {

	@Id
	@ManyToOne
	@JoinColumn(name = "form_id", referencedColumnName = "id", nullable = false)
	private Form form;

	@Id
	@javax.persistence.Column(name = "column_type", nullable = false)
	private ColumnType columnType;

	@JsonIgnoreProperties("column")
	@OneToMany(mappedBy = "column", cascade = CascadeType.ALL)
	private List<Box> boxes;

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

	public List<Box> getBoxes() {
		Collections.sort(boxes, (a, b) -> a.getBoxType().ordinal() < b.getBoxType().ordinal() ? -1
				: a.getBoxType().ordinal() == b.getBoxType().ordinal() ? 0 : 1);
		return boxes;
	}

	public void setBoxes(List<Box> boxes) {
		this.boxes = boxes;
	}

	public Box getBoxByType(BoxType boxType) {
		Box box = new Box();
		for (Box b : boxes) {
			if (b.getBoxType() == boxType) {
				box = b;
				break;
			}
		}
		return box;
	}

	public boolean isCompleted() {
		for (Box box : boxes) {
			if (!box.isFilled())
				return false;
		}
		return true;
	}

	public int calculateSum() {
		int numberSum = 0;
		int diffSum = 0;
		int labelSum = 0;
		boolean diffReady = true;
		for (Box box : boxes) {
			if (box.getBoxType() == BoxType.ONES || box.getBoxType() == BoxType.MAX
					|| box.getBoxType() == BoxType.MIN) {
				if (!box.isFilled())
					diffReady = false;
			}
			if (box.getBoxType().ordinal() <= 5)
				numberSum += box.getValue();
			else if (box.getBoxType().ordinal() >= 8)
				labelSum += box.getValue();
		}
		if (numberSum >= JambConstants.NUMBERSUM_BONUS_THRESHOLD)
			numberSum += JambConstants.NUMBERSUM_BONUS;
		if (diffReady)
			diffSum = (getBoxByType(BoxType.MAX).getValue() - getBoxByType(BoxType.MIN).getValue())
					* getBoxByType(BoxType.ONES).getValue();
		return numberSum + diffSum + labelSum;
	}

	public Map<String, Integer> calculateSums() {
		Map<String, Integer> sums = new HashMap<>();
		sums.put("numberSum", 0);
		sums.put("diffSum", 0);
		sums.put("labelSum", 0);

		boolean diffReady = true;
		for (Box box : boxes) {
			if (box.getBoxType() == BoxType.ONES || box.getBoxType() == BoxType.MAX
					|| box.getBoxType() == BoxType.MIN) {
				if (!box.isFilled()) {
					diffReady = false;
				}
			}
			if (box.getBoxType().ordinal() <= 5) {
				sums.replace("numberSum", sums.get("numberSum") + box.getValue());
			} else if (box.getBoxType().ordinal() >= 8) {
				sums.replace("labelSum", sums.get("labelSum") + box.getValue());
			}
		}
		if (sums.get("numberSum") >= JambConstants.NUMBERSUM_BONUS_THRESHOLD)
			sums.replace("numberSum", sums.get("numberSum") + JambConstants.NUMBERSUM_BONUS);
		if (diffReady) {
			sums.replace("diffSum", (getBoxByType(BoxType.MAX).getValue() - getBoxByType(BoxType.MIN).getValue())
					* getBoxByType(BoxType.ONES).getValue());
		}
		return sums;
	}
	
	@Override
	public String toString() {
		String string = columnType + ":\n";
		for (Box box : boxes) {
			string += box + "\n";
		}
		return string;
	}
}
