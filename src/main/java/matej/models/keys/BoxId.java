package matej.models.keys;

import java.io.Serializable;
import java.util.Objects;

import matej.models.GameColumn;
import matej.models.types.BoxType;

public class BoxId implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private GameColumn column;

    private BoxType boxType;

    public BoxId() {
    }

    public BoxId(GameColumn column, BoxType boxType) {
        this.column = column;
        this.boxType = boxType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BoxId that = (BoxId) o;

        return Objects.equals(column, that.column) && Objects.equals(boxType, that.boxType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, boxType);
    }
}