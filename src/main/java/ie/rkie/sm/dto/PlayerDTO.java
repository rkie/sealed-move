package ie.rkie.sm.dto;

public class PlayerDTO implements Comparable<PlayerDTO>{

	private String username;
	
	private Integer playOrder;

	public PlayerDTO(String username, Integer playOrder) {
		super();
		this.username = username;
		this.playOrder = playOrder;
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
}
