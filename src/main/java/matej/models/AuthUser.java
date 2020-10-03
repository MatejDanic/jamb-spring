package matej.models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "auth_user")
public class AuthUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}) 
    private List<GameScore> scores;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private GameForm form;
    
    @Column(name = "username", nullable = false, unique = true)
    @Size(min = 3, max = 15)
    private String username;
    
    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "auth_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<AuthRole> roles;

    public AuthUser() {
    }

    public AuthUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public List<GameScore> getScores() {
        return scores;
    }

    public void setScores(List<GameScore> scores) {
        this.scores = scores;
    }

    public GameForm getForm() {
        return form;
    }

    public void setForm(GameForm form) {
        this.form = form;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
        
    public Set<AuthRole> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<AuthRole> roles) {
        this.roles = roles;
    }

	@Override
	public String toString() {
        String string = username;
        for (AuthRole role : roles) {
            string += role.getLabel() + ": " + role.getdescription() + "\n";
        }
		return string;
	}
}