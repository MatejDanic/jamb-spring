package matej.models;

import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import matej.models.keys.DiceId;

@Entity
@Table(name="game_dice")
@IdClass(DiceId.class)
public class GameDice {

	@Id
	@ManyToOne
	@JoinColumn(name = "form_id", referencedColumnName = "id", nullable = false)
	private GameForm form;
	
	@Id
	@Column(name = "ordinal_number")
	private int ordinalNumber;

	@javax.persistence.Column(name = "value")
	private int value;

	public GameForm getForm() {
		return form;
	}

	public void setForm(GameForm form) {
		this.form = form;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public int getOrdinalNumber() {
		return ordinalNumber;
	}

	public void setOrdinalNumber(int ordinalNumber) {
		this.ordinalNumber = ordinalNumber;
	}

	public void roll() {
		value = ThreadLocalRandom.current().nextInt(1, 7);
	}
	
	@Override
	public String toString() {
		return "D#" + ordinalNumber + ": " + value;
	}
}