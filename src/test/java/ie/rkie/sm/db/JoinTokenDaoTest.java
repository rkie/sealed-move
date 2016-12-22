package ie.rkie.sm.db;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the dao and entities using in hsqldb.
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class JoinTokenDaoTest {
	
	@Autowired
	private JoinTokenDao dao;
	
	@Autowired
	private GameDao gameDao;

	@Test
	public void testFindByToken() {
		List<JoinToken> tokens = dao.findByToken("UNIQUE_GAME_ENTRY_TOKEN");
		assertThat(tokens, hasSize(1));
	}
	
	@Test
	public void testFindByTokenInvalid() {
		List<JoinToken> tokens = dao.findByToken("TOKEN WON'T BE FOUND");
		assertThat(tokens, hasSize(0));
	}
	
	@Test
	public void testWalkToGame() {
		List<JoinToken> tokens = dao.findByToken("UNIQUE_GAME_ENTRY_TOKEN");
		assertThat(tokens, hasSize(1));
		JoinToken token = tokens.get(0);
		Game game = token.getGame();
		assertThat(game, notNullValue());
		assertThat(game.getGameType(), notNullValue());
		assertThat(game.getGameType().getDisplayName(), is("Chess"));
		assertThat(game.getOwner(), notNullValue());
		assertThat(game.getOwner().getUsername(), is("bob"));
		assertThat(game.getStatus(), is("SETUP"));
	}

	@Test
	public void testFindOneByGame() {
		Game game = gameDao.findOne(0);
		JoinToken token = dao.findOneByGame(game);
		assertThat(token, notNullValue());
		assertThat(token.getToken(), is("UNIQUE_GAME_ENTRY_TOKEN"));
	}

}
