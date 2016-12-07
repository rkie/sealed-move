package ie.rkie.sm.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="game_type")
public class GameType {
	
	@Id
	@GeneratedValue
	private int id;
	
	private String name;
	
	private String displayName;
	
	private int minPlayers;
	
	private int maxPlayers;
	
	public GameType() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	@Override
	public String toString() {
		return String
				.format("GameType [id=%s, \nname=%s, \ndisplayName=%s, \nminPlayers=%s, \nmaxPlayers=%s]",
						id, name, displayName, minPlayers, maxPlayers);
	}

}
