package ie.rkie.sm.service;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameType;
import ie.rkie.sm.db.User;
import ie.rkie.sm.dto.GameSelectionDTO;
import ie.rkie.sm.dto.PlayerDTO;

import java.util.List;

public interface GameService {

	List<GameSelectionDTO> gameList();
	
	Game startGame(GameType gameType, int players, User owner);
	
	String createToken(Game game);
	
	GameType fromName(String name);
	
	/**
	 * Attempt to make the player join the game associated with the provided
	 * token. There are various results possible given by the return enum.
	 * @param player
	 * @param token
	 * @return
	 */
	JoinAttemptResult joinWithToken(User player, String token);

	/**
	 * Create a list of {@link PlayerDTO} for presentation.
	 * @param game
	 * @return
	 */
	List<PlayerDTO> players(Game game);

}
