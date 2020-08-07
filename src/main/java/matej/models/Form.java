package matej.models;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import matej.models.enums.BoxType;
import matej.models.enums.ColumnType;


@Entity
@Table(name="form")
public class Form {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@OneToMany(mappedBy ="form", cascade = CascadeType.ALL)
	private List<Column> columns;

	@OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
    private List<Dice> dice;

	@javax.persistence.Column(name = "roll_count", nullable = false)
	private int rollCount;
	
	@javax.persistence.Column(name = "announcement", nullable = true)
	private BoxType announcement;

	@javax.persistence.Column(name = "announcement_required", nullable = false)
	private boolean announcementRequired;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Dice> getDice() {
		return dice;
	}

	public void setDice(List<Dice> dice) {
		this.dice = dice;
	}

	public int getRollCount() {
		return rollCount;
	}

	public void setRollCount(int rollCount) {
		this.rollCount = rollCount;
	}

	public BoxType getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(BoxType announcement) {
		this.announcement = announcement;
	}
	
	public Column getColumnByType(ColumnType columnType) {
		Column column = new Column();
		for (Column fc : columns) {
			if (fc.getColumnType() == columnType) {
				column = fc;
				break;
			}
		}
		return column;
	}

	public Dice getDiceByOrdinalNumber(int ordinalNumber) {
		Dice newDice = new Dice();
		for (Dice d : dice) {
			if (d.getLabel() == ordinalNumber) newDice = d;
			break;
		}
		return newDice;
	}

	public boolean isAnnouncementRequired() {
		return announcementRequired;
	}

	public Map<String, Integer> calculateSums() {
		Map<String, Integer> sums = new HashMap<>();
		Map<String, Integer> columnSums;
		sums.put("numberSum", 0);
		sums.put("diffSum", 0);
		sums.put("labelSum", 0);
		for (Column column : columns) {
			columnSums = column.calculateSums();
			columnSums.forEach((k, v) -> sums.put(column.getColumnType().toString() + "-" + k, v));
			sums.replace("numberSum", sums.get("numberSum") + columnSums.get("numberSum"));
			sums.replace("diffSum", sums.get("diffSum") + columnSums.get("diffSum"));
			sums.replace("labelSum", sums.get("labelSum") + columnSums.get("labelSum"));
		}
		sums.put("finalSum", sums.get("numberSum") + sums.get("diffSum") + sums.get("labelSum"));
		return sums;
	}
	
	// @Override
	// public String toString() {
	// 	String result = "";
	// 	for (Column column : columns) {
	// 		for (Box box : column.getBoxes()) {
	// 			result += " " + box.getValue();
	// 		}
	// 		result += "\n";
	// 	}
	// 	return result;
	// }

	public void setAnnouncementRequired(boolean announcementRequired) {
		this.announcementRequired = announcementRequired;
	}
}
