package matej.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.rest.core.annotation.RestResource;

@Entity
@Table(name = "preference")
@RestResource(rel = "preferences", path = "preferences")
public class Preference {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne
    @JsonIgnore
	@JoinColumn(name = "user_id", nullable = false)
	private AuthUser user;
    
	@Column(name = "volume")
    private int volume;

    public AuthUser getUser() {
        return user;
    }

    public void setUser(AuthUser user) {
        this.user = user;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}