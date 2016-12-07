package ie.rkie.sm.service;

import ie.rkie.sm.db.Game;

public interface TokenFactory {
	
	String createToken(Game game);

}
