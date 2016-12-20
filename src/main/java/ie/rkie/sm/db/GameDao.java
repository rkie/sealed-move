package ie.rkie.sm.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface GameDao extends CrudRepository<Game, Integer> {

	List<Game> findByOwner(User owner);

}
