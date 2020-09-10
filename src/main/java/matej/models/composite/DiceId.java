package matej.models.composite;

import java.io.Serializable;
import java.util.Objects;

import matej.models.Form;

public class DiceId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Form form;
	
	private int ordinalNumber;
	
	public DiceId() {}
 
    public DiceId(Form form, int ordinalNumber) {
        this.form = form;
        this.ordinalNumber = ordinalNumber;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this== o) return true;
        if (o ==null|| getClass() != o.getClass()) return false;

        DiceId that = (DiceId) o;

        return Objects.equals(form, that.form) 
        		&& Objects.equals(ordinalNumber, that.ordinalNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(form, ordinalNumber);
    }
}