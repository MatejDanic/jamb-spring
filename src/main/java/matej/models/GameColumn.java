package matej.models;

import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import matej.constants.GameConstants;
import matej.models.keys.ColumnId;
import matej.models.types.ColumnType;

@Entity
@Table(name = "game_column")
@IdClass(ColumnId.class)
public class GameColumn {

	@Id
	@ManyToOne
	@JoinColumn(name = "form_id", referencedColumnName = "id", nullable = false)
	private GameForm form;

	@Id
	@ManyToOne
	@JoinColumn(name = "column_type", referencedColumnName = "id", nullable = false)
	private ColumnType columnType;

	@JsonIgnoreProperties("column")
	@OneToMany(mappedBy = "column", cascade = CascadeType.ALL)
	private List<GameBox> boxes;

	public GameForm getForm() {
		return form;
	}

	public void setForm(GameForm form) {
		this.form = form;
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

	public List<GameBox> getBoxes() {
		Collections.sort(boxes, (a, b) -> a.getBoxType().getId() < b.getBoxType().getId() ? -1
				: a.getBoxType().getId() == b.getBoxType().getId() ? 0 : 1);
		return boxes;
	}

	public void setBoxes(List<GameBox> boxes) {
		this.boxes = boxes;
	}

	public GameBox getBoxByTypeId(int boxTypeId) {
		GameBox box = new GameBox();
		for (GameBox b : boxes) {
			if (b.getBoxType().getId() == boxTypeId) {
				box = b;
				break;
			}
		}
		return box;
	}

	public GameBox getBoxByTypeLabel(String boxTypeLabel) {
		GameBox box = new GameBox();
		for (GameBox b : boxes) {
			if (b.getBoxType().getLabel().equals(boxTypeLabel)) {
				box = b;
				break;
			}
		}
		return box;
	}

	@JsonIgnore
	public boolean isCompleted() {
		for (GameBox box : boxes) {
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
		for (GameBox box : boxes) {			
			if (box.getBoxType().getId() <= 6)
				numberSum += box.getValue();
			else if (box.getBoxType().getId() >= 9)
				labelSum += box.getValue();
			if (box.getBoxType().getLabel().equals("ONES") || box.getBoxType().getLabel().equals("MAX")
				|| box.getBoxType().getLabel().equals("MIN")) {
			if (!box.isFilled())
				diffReady = false;
		}
		}
		if (numberSum >= GameConstants.NUMBERSUM_BONUS_THRESHOLD)
			numberSum += GameConstants.NUMBERSUM_BONUS;
		if (diffReady)
			diffSum = (getBoxByTypeLabel("MAX").getValue() - getBoxByTypeLabel("MIN").getValue())
					* getBoxByTypeLabel("ONES").getValue();
		return numberSum + diffSum + labelSum;
	}
	
	@Override
	public String toString() {
		String string = columnType + ":\n";
		for (GameBox box : boxes) {
			string += box + "\n";
		}
		return string;
	}
}
