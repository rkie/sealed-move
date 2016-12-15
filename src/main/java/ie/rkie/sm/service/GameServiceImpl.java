package ie.rkie.sm.service;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.GameType;
import ie.rkie.sm.db.GameTypeDao;
import ie.rkie.sm.db.JoinToken;
import ie.rkie.sm.db.JoinTokenDao;
import ie.rkie.sm.db.Player;
import ie.rkie.sm.db.User;
import ie.rkie.sm.dto.GameSelectionDTO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {
	
	@Autowired
	private GameTypeDao gameTypeDao;
	
	@Autowired
	private GameDao gameDao;
	
	@Autowired
	private JoinTokenDao joinTokenDao;
	
	// injectable???
	private TokenFactory tokenFactory;

	public GameServiceImpl() {
		tokenFactory = new SimpleTokenFactory();
	}

	@Override
	public List<GameSelectionDTO> gameList() {
		List<GameSelectionDTO> games = new ArrayList<GameSelectionDTO>();
		for ( GameType gameType : gameTypeDao.findAll() ) {
			GameSelectionDTO dto = new GameSelectionDTO(gameType.getName(),
					gameType.getDisplayName(), gameType.getMinPlayers(),
					gameType.getMaxPlayers());
			games.add(dto);
		}
		return games;
	}

	@Override
	public Game startGame(GameType gameType, int players, User owner) {
		Game game = new Game();
		game.setGameType(gameType);
		game.setOwner(owner);
		game.setStatus("SETUP");
		// TODO: We need to add the players here...
		game = gameDao.save(game);
		return game;
	}

	@Override
	public String createToken(Game game) {
		JoinToken token = new JoinToken();
		token.setGame(game);
		token.setToken(tokenFactory.createToken(game));
		joinTokenDao.save(token);
		return token.getToken();
	}

	@Override
	public GameType fromName(String name) {
		List<GameType> games = gameTypeDao.findByName(name);
		// TODO: need to protect against unknown games
		return games.get(0);
	}

	/**
	 * Convenience method to look up the game by token.
	 * @param token
	 * @return
	 */
	private Game from(String token) {
		List<JoinToken> joinToken = joinTokenDao.findByToken(token);
		if ( joinToken.size() == 0 ) {
			return null;
		}
		return joinToken.get(0).getGame();
	}

	@Override
	public JoinAttemptResult joinWithToken(User player, String token) {

		// Check for the token to find the game
		Game game = from(token);
		if ( game == null ) {
			return JoinAttemptResult.GAME_NOT_FOUND;
		}
		// Make sure this user has not already joined
		List<Player> players = game.getPlayers();
		for ( Player existingPlayer : players ) {
			if ( existingPlayer.getUser().getUsername().equals(player.getUsername()) ) {
				return JoinAttemptResult.ALREADY_JOINED;
			}
		}

		// Make sure the game is not fully subscribed
		int max = game.getGameType().getMaxPlayers();
		if ( players.size() >= max ) {
			return JoinAttemptResult.PLAYER_LIMIT_REACHED;
		}

		// TODO: Add the user to the game
		return JoinAttemptResult.SUCCESS;
	}
	
}
