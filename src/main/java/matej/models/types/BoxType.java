
package matej.models.types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "box_type")
public class BoxType {

	@Column(name = "id", nullable = false)
	private int id;

	@Id
	@Column(name = "label", nullable = false)
	private String label;

	public BoxType(int id, String label) {
		this.id = id;
		this.label = label;
	}

	public BoxType() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

}
