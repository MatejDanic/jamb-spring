
package matej.models.types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;

@Entity
@Table(name = "box_type")
@RestResource(rel = "box-types", path = "box-types")
public class BoxType {

	@Id
	private int id;

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
