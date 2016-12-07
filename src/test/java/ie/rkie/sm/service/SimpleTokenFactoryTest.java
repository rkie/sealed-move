package ie.rkie.sm.service;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Base64;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameType;
import ie.rkie.sm.db.User;

import org.junit.Before;
import org.junit.Test;

public class SimpleTokenFactoryTest {
	
	private SimpleTokenFactory factory;
	private Game game1;
	private Game game2;
	
	@Before
	public void before() {
		factory = new SimpleTokenFactory();
		game1 = new Game();
		game1.setGid(100);
		GameType type = new GameType();
		type.setName("chess");
		game1.setGameType(type);
		User owner = new User("email", "username");
		game1.setOwner(owner);
		game1.setStatus("SETUP");
		
		game2 = new Game();
		game2.setGid(101);
		type.setName("chess");
		game2.setGameType(type);
		game2.setOwner(owner);
		game2.setStatus("SETUP");
	}
	
	/**
	 * A bit arbitrary but should flag any changes to the initial implementation.
	 */
	@Test
	public void testTokenGeneration() {
		final String expected = "dXNlcm5hbWUsMTAwLGNoZXNz";
		
		// test
		String actual = factory.createToken(game1);
		
		// check
		assertThat(actual, is(expected));
	}
	
	/**
	 * Different game ids should generate different tokens
	 */
	@Test
	public void testTokensDifferent() {
		// test
		String actual1 = factory.createToken(game1);
		String actual2 = factory.createToken(game2);
		
		// check
		assertThat(actual1, not(is(actual2)));
	}

	/**
	 * Should be able to decode a token.
	 */
	@Test
	public void testDecodeToken() {
		final String expected = "username,100,chess";

		String actual = factory.createToken(game1);
		String result = new String(Base64.getDecoder().decode(actual));

		assertThat(result, is(expected));
	}
}
