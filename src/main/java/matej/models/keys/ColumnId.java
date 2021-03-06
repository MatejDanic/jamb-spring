package matej.models.keys;

import java.io.Serializable;
import java.util.Objects;

import matej.models.GameForm;
import matej.models.types.ColumnType;

public class ColumnId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GameForm form;
	
	private ColumnType columnType;
	
	public ColumnId() {}
 
    public ColumnId(GameForm form, ColumnType columnType) {
        this.form = form;
        this.columnType = columnType;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this== o) return true;
        if (o ==null|| getClass() != o.getClass()) return false;

        ColumnId that = (ColumnId) o;

        return Objects.equals(form, that.form) 
        		&& Objects.equals(columnType, that.columnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(form, columnType);
    }
}