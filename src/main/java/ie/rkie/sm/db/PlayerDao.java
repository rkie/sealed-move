package ie.rkie.sm.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PlayerDao extends CrudRepository<Player, Integer> {
	
	List<Player> findByGame(Game game);

	List<Player> findByUser(User user);

	Player findOneByGameAndPlayOrder(Game game, int playOrder);

}
