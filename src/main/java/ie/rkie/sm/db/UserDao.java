package ie.rkie.sm.db;

import org.springframework.data.repository.CrudRepository;

/**
 * Provides simple access to the underlying table.
 *
 */
public interface UserDao extends CrudRepository<User, Long> {

}
