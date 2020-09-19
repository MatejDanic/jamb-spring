package matej.models;

import java.util.Collections;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import matej.models.enums.ColumnType;


@Entity
@Table(name="form")
public class Form {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne
    @JsonIgnoreProperties({"scores", "form"})
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@JsonIgnoreProperties("form")
	@OneToMany(mappedBy ="form", cascade = CascadeType.ALL)
	private List<Column> columns;

	@JsonIgnoreProperties("form")
	@OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
    private List<Dice> dice;

	@javax.persistence.Column(name = "roll_count", nullable = false)
	private int rollCount;
	
	@javax.persistence.Column(name = "announcement", nullable = true)
	private Integer announcement;

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
		Collections.sort(columns, (a, b) -> a.getColumnType().ordinal() < b.getColumnType().ordinal() ? -1 : a.getColumnType().ordinal() == b.getColumnType().ordinal() ? 0 : 1);
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Dice> getDice() {
		Collections.sort(dice, (a, b) -> a.getOrdinalNumber() < b.getOrdinalNumber() ? -1 : a.getOrdinalNumber() == b.getOrdinalNumber() ? 0 : 1);
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

	public Integer getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Integer announcement) {
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
			if (d.getOrdinalNumber() == ordinalNumber) newDice = d;
			break;
		}
		return newDice;
	}

	public int calculateFinalSum() {
		int finalSum = 0;
		for (Column column : columns) {
			finalSum += column.calculateSum();
		}
		return finalSum;
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

	public boolean checkAnnouncementRequired() {
		boolean announcementRequired = (announcement == null);
		for (Column column : columns) {
			if (column.getColumnType() != ColumnType.ANNOUNCEMENT) {
				if (!column.isCompleted()) {
					announcementRequired = false;
					break;
				}
			}
		}
		return announcementRequired;
	}

	@Override
	public String toString() {
		String string = "User: " + user;
		string += "\nColumns:\n";
		for (Column column : columns) {
			string += column + "\n";
		}
		string += "\nDice:";
		for (Dice d : dice) {
			string += d + " ";
		}
		return string;
	}
}
