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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import matej.models.types.BoxType;

@Entity
@Table(name="game_form")
public class GameForm {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne
    @JsonIgnoreProperties({"scores", "form"})
	@JoinColumn(name = "user_id", nullable = false)
	private AuthUser user;
	
	@JsonIgnoreProperties("form")
	@OneToMany(mappedBy ="form", cascade = CascadeType.ALL)
	private List<GameColumn> columns;

	@JsonIgnoreProperties("form")
	@OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
    private List<GameDice> dice;

	@javax.persistence.Column(name = "roll_count", nullable = false)
	private int rollCount;
	
	@ManyToOne
	@JoinColumn(name = "announcement", referencedColumnName = "label", nullable = true)
	private BoxType announcement;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AuthUser getUser() {
		return user;
	}

	public void setUser(AuthUser user) {
		this.user = user;
	}

	public List<GameColumn> getColumns() {
		Collections.sort(columns, (a, b) -> a.getColumnType().getId() < b.getColumnType().getId() ? -1 : a.getColumnType().getId() == b.getColumnType().getId() ? 0 : 1);
		return columns;
	}

	public void setColumns(List<GameColumn> columns) {
		this.columns = columns;
	}

	public List<GameDice> getDice() {
		Collections.sort(dice, (a, b) -> a.getOrdinalNumber() < b.getOrdinalNumber() ? -1 : a.getOrdinalNumber() == b.getOrdinalNumber() ? 0 : 1);
		return dice;
	}

	public void setDice(List<GameDice> dice) {
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

	public GameColumn getColumnByTypeId(int columnTypeId) {
		GameColumn column = new GameColumn();
		for (GameColumn fc : columns) {
			if (fc.getColumnType().getId() == columnTypeId) {
				column = fc;
				break;
			}
		}
		return column;
	}

	public GameDice getDiceByOrdinalNumber(int ordinalNumber) {
		GameDice newDice = new GameDice();
		for (GameDice d : dice) {
			if (d.getOrdinalNumber() == ordinalNumber) newDice = d;
			break;
		}
		return newDice;
	}

	public int calculateFinalSum() {
		int finalSum = 0;
		for (GameColumn column : columns) {
			finalSum += column.calculateSum();
		}
		return finalSum;
	}

	@JsonIgnore
	public boolean isCompleted() {
		for (GameColumn column : columns) {
			if (!column.isCompleted())
				return false;
		}
		return true;
	}

	public Map<String, Integer> calculateSums() {
		Map<String, Integer> sums = new HashMap<>();
		Map<String, Integer> columnSums;
		sums.put("numberSum", 0);
		sums.put("diffSum", 0);
		sums.put("labelSum", 0);
		for (GameColumn column : columns) {
			columnSums = column.calculateSums();
			columnSums.forEach((k, v) -> sums.put(column.getColumnType().toString() + "-" + k, v));
			sums.replace("numberSum", sums.get("numberSum") + columnSums.get("numberSum"));
			sums.replace("diffSum", sums.get("diffSum") + columnSums.get("diffSum"));
			sums.replace("labelSum", sums.get("labelSum") + columnSums.get("labelSum"));
		}
		sums.put("finalSum", sums.get("numberSum") + sums.get("diffSum") + sums.get("labelSum"));
		return sums;
	}

	@JsonIgnore
	public boolean isAnnouncementRequired() {
		boolean announcementRequired = true;
		for (GameColumn column : columns) {
			if (column.getColumnType().getLabel().equals("ANNOUNCEMENT")) {
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
		for (GameColumn column : columns) {
			string += column + "\n";
		}
		string += "\nDice:";
		for (GameDice d : dice) {
			string += d + " ";
		}
		return string;
	}
}
