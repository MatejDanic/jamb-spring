
package matej.models.types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// ONES, TWOS, THREES, FOURS, FIVES, SIXES, MAX, MIN, TRIPS, STRAIGHT, FULL,
	// POKER, JAMB;

@Entity
@Table(name="box_type")
public class BoxType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "label", nullable = false)
	private String label;

	public BoxType(String label) {
		this.label = label;
	}

	private BoxType() { }

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

}
