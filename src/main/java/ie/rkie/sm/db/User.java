package ie.rkie.sm.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Represents a user that can start or participate in a game.
 *
 */
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long uid;

	// The user's email
	@NotNull
	private String email;
	
	// The user's name
	@NotNull
	private String username;

	public User() {
		super();
	}
	
	public User(long uid) {
		super();
		this.uid = uid;
	}

	public User(String email, String username) {
		super();
		this.email = email;
		this.username = username;
	}

	public long getUid() {
		return uid;
	}

	public void setId(long uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
