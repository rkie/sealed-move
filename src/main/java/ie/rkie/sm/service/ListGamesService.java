package ie.rkie.sm.service;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.User;

import java.util.List;

/**
 * Various ways to list the games a user may be interested in.
 *
 */
public interface ListGamesService {
	
	/**
	 * Where this user created the game and it has not finished.
	 * @param user
	 * @return
	 */
	List<Game> listActiveOwnedGames(User user);
	
	/**
	 * Games this user has joined and that are not finished.
	 * @param user
	 * @return
	 */
	List<Game> listActiveJoinedGames(User user);
	
	/**
	 * Finished games this user created.
	 * @param user
	 * @return
	 */
	List<Game> listFinishedOwnedGames(User user);
	
	/**
	 * Finished games this user joined.
	 * @param user
	 * @return
	 */
	List<Game> listFinishedJoinedGames(User user);

}
