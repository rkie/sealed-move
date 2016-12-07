package ie.rkie.sm.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface GameTypeDao extends CrudRepository<GameType, Integer> {
	
	List<GameType> findByName(String name);

}
