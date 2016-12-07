package ie.rkie.sm.service;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameType;
import ie.rkie.sm.db.User;
import ie.rkie.sm.dto.GameSelectionDTO;

import java.util.List;

public interface GameService {

	List<GameSelectionDTO> gameList();
	
	Game startGame(GameType gameType, int players, User owner);
	
	String createToken(Game game);
	
	GameType fromName(String name);
	
}
