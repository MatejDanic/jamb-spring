package matej.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="game_score")
public class GameScore {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
    @JsonIgnoreProperties({"scores", "form"})
	@JoinColumn(name = "user_id", nullable = false)
	private AuthUser user;
	
	@Column(name = "value", nullable = false)
	private int value;
	
	@Column(name = "date", nullable = false)
	private LocalDateTime date;

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
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public LocalDateTime getDate() {
		return date;
	}
	
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
