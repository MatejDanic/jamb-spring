package matej.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import matej.models.keys.BoxId;
import matej.models.types.BoxType;
import matej.utils.ScoreUtil;

@Entity
@Table(name = "game_box")
@IdClass(BoxId.class)
public class GameBox {

	@Id
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "form_id", referencedColumnName = "form_id", nullable = false),
				  @JoinColumn(name = "column_type_id", referencedColumnName = "column_type", nullable = false) })
	private GameColumn column;

	@Id
	@ManyToOne
	@JoinColumn(name = "box_type", referencedColumnName = "id", nullable = false)
	private BoxType boxType;

	@Column(name = "value")
	private int value;

	@Column(name = "filled")
	private boolean filled;

	@Column(name = "available")
	private boolean available;

	public GameColumn getColumn() {
		return column;
	}

	public void setColumn(GameColumn column) {
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

	public int fill(List<GameDice> diceList) {
		value = ScoreUtil.calculateScore(diceList, boxType);
		filled = true;
		available = false;
		return value;
	}

	@Override
	public String toString() {
		return boxType + ": " + value + " (FILLED-" + filled + ", AVAILABLE-" + available + ")";
	}
}
