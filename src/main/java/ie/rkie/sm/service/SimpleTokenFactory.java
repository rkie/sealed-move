package ie.rkie.sm.service;

import ie.rkie.sm.db.Game;

import java.util.Base64;
import java.util.Base64.Encoder;

/**
 * Creates a string that can be used in a url that obfuscates some of the unique
 * data for the request.
 *
 */
public class SimpleTokenFactory implements TokenFactory {
	
	private Encoder encoder;

	public SimpleTokenFactory() {
		super();
		encoder = Base64.getEncoder();
	}

	@Override
	public String createToken(Game game) {
		StringBuilder b = new StringBuilder()
				.append(game.getOwner().getUsername()).append(",")
				.append(game.getGid()).append(",")
				.append(game.getGameType().getName());
		String token =  new String(encoder.encode(b.toString().getBytes()));
		return token;
	}

}
