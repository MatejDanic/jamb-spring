package matej.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;

@Entity
@Table(name = "auth_role")
@RestResource(rel = "roles", path = "roles")
public class AuthRole {
    
    @Id
	private int id;

	@Column(name = "label")
	private String label;

	@Column(name = "description")
    private String description;

    public AuthRole() {
    }

    public AuthRole(int id, String label) {
        this.id = id;
        this.label = label;
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

    public String getdescription() {
        return description;
    }

    public void setdescription(String description) {
        this.description = description;
    }
    
}