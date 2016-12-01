package ie.rkie.sm.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface JoinTokenDao extends CrudRepository<JoinToken, Integer> {

	List<JoinToken> findByToken(String token);
}
