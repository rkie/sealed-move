package ie.rkie.sm.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * Provides simple access to the underlying table.
 *
 */
public interface UserDao extends CrudRepository<User, String> {
	
	List<User> findByEmail(String email);

}
