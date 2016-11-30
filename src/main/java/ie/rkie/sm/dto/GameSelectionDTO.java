package ie.rkie.sm.dto;
/**
 * Holds the games available and some of the options possible.
 *
 */
public class GameSelectionDTO {

	private String name;
	private String displayName;
	private int minPlayers;
	private int maxPlayers;

	public GameSelectionDTO(String name, String displayName, int minPlayers, int maxPlayers) {
		super();
		this.name = name;
		this.displayName = displayName;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
