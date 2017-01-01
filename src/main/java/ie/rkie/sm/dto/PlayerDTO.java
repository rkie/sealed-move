package ie.rkie.sm.dto;

public class PlayerDTO implements Comparable<PlayerDTO>{

	private String username;
	
	private Integer playOrder;

	private boolean moveUp;

	private boolean moveDown;

	public PlayerDTO(String username, Integer playOrder, int numPlayers) {
		super();
		this.username = username;
		this.playOrder = playOrder;
		this.moveUp = playOrder < numPlayers;
		this.moveDown = playOrder > 1;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getPlayOrder() {
		return playOrder;
	}

	public void setPlayOrder(Integer playOrder) {
		this.playOrder = playOrder;
	}

	@Override
	public String toString() {
		return String.format("PlayerDTO [username=%s, playOrder=%s]",
				username, playOrder);
	}

	@Override
	public int compareTo(PlayerDTO other) {
		return playOrder.compareTo(other.playOrder);
	}

	public boolean isMoveUp() {
		return moveUp;
	}

	public boolean isMoveDown() {
		return moveDown;
	}
}
