package ie.rkie.sm.db;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring generated the dao methods based on the signature - return type, method name and
 * the parameters.
 *
 */
public interface GameDao extends JpaRepository<Game, Integer> {

	/**
	 * Games owned by the owner.
	 * @param owner
	 * @return
	 */
	List<Game> findByOwner(User owner);

	/**
	 * Either games owned by the  ownerName or joined by the playerName, returned as pages.
	 * @param pageble
	 * @param ownerName
	 * @param playerName
	 * @return
	 */
	Page<Game> findByOwnerUsernameOrPlayersUserUsername(Pageable pageble, String ownerName, String playerName);

	/**
	 * Either games owned by the  ownerName or joined by the playerName.
	 * @param ownerName
	 * @param playerName
	 * @return
	 */
	List<Game> findByOwnerUsernameOrPlayersUserUsername(String ownerName, String playerName);

	/**
	 * Games owned by this username.
	 * @param username
	 * @return
	 */
	List<Game> findByOwnerUsername(String username);

	/**
	 * Games joined by the username.
	 * @param username
	 * @return
	 */
	List<Game> findByPlayersUserUsername(String username);

}
