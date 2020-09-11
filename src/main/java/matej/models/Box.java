package matej.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import matej.models.composite.BoxId;
import matej.models.enums.BoxType;
import matej.utils.ScoreUtil;

@Entity
@Table(name = "box")
@IdClass(BoxId.class)
public class Box {

	@Id
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "form_id", referencedColumnName = "form_id", nullable = false),
				  @JoinColumn(name = "column_type", referencedColumnName = "column_type", nullable = false) })
	private Column column;

	@Id
	@javax.persistence.Column(name = "box_type")
	private BoxType boxType;

	@javax.persistence.Column(name = "value")
	private int value;

	@javax.persistence.Column(name = "filled")
	private boolean filled;

	@javax.persistence.Column(name = "available")
	private boolean available;

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public BoxType getBoxType() {
		return boxType;
	}

	public void setBoxType(BoxType boxType) {
		this.boxType = boxType;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int fill(List<Dice> diceList) {
		value = ScoreUtil.calculateScore(diceList, boxType);
		filled = true;
		available = false;
		return value;
	}

	@Override
	public String toString() {
		return boxType + ": " + value + "(filled-" + filled + ", available-" + available + ")";
	}
}
