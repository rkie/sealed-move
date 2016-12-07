package ie.rkie.sm.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="games")
public class Game {

	@Id
	@GeneratedValue
	private Integer gid;
	
	@ManyToOne
	@JoinColumn(name="type_id")
	private GameType gameType;
	
	@ManyToOne
	@JoinColumn(name="owner")
	private User owner;
	
	private String status;

	public Game() {
		super();
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format(
				"Game [gid=%s, \ngameType=%s, \nowner=%s, \nstatus=%s]", gid,
				gameType, owner, status);
	}
	
}
