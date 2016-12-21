package ie.rkie.sm.dto;

/**
 * This DTO facilitates displaying game details within the templates.
 *
 */
public class GameDTO {
	
	private String displayName;
	
	public GameDTO(String displayName) {
		super();
		this.displayName = displayName;
	}

	private String playerList;
	
	private String status;
	
	private Integer gameId;

	public GameDTO(String displayName, String playerList, String status,
			Integer gameId) {
		super();
		this.displayName = displayName;
		this.playerList = playerList;
		this.status = status;
		this.gameId = gameId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public String getPlayerList() {
		return playerList;
	}

	public void setPlayerList(String playerList) {
		this.playerList = playerList;
	}

}
