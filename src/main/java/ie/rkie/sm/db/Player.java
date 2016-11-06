package ie.rkie.sm.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Represents a player that can start or participate in a game.
 *
 */
@Entity
@Table(name = "players")
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long pid;

	// The user's email
	@NotNull
	private String email;
	
	// The user's name
	@NotNull
	private String username;

	public Player() {
		super();
	}
	
	public Player(long pid) {
		super();
		this.pid = pid;
	}

	public Player(String email, String username) {
		super();
		this.email = email;
		this.username = username;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
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
