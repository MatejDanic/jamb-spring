package matej.models;

import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import matej.models.composite.DiceId;

@Entity
@Table(name="dice")
@IdClass(DiceId.class)
public class Dice {

	@Id
	@ManyToOne
	@JoinColumn(name = "form_id", referencedColumnName = "id", nullable = false)
	private Form form;
	
	@Id
	@javax.persistence.Column(name = "ordinal_number")
	private int label;

	@javax.persistence.Column(name = "value")
	private int value;

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public void roll() {
		value = ThreadLocalRandom.current().nextInt(1, 7);
	}
	
	@Override
	public String toString() {
		return form.getId() + " Dice #" + label + ": " + value;
	}
	
}
