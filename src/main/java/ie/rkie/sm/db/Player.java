package ie.rkie.sm.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A player represents the link between a user and a game. The playOrder
 * property allows the controller to know which player has the next move. 
 */
@Entity
@Table(name="players")
public class Player {

	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="gid")
	private Game game;
	
	@ManyToOne
	@JoinColumn(name="username")
	private User user;
	
	private int playOrder;

	public Player() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getPlayOrder() {
		return playOrder;
	}

	public void setPlayOrder(int playOrder) {
		this.playOrder = playOrder;
	}
	
}
