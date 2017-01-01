package ie.rkie.sm.db;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlayerDaoTest {
	
	@Autowired
	private PlayerDao playerDao;
	
	@Autowired
	private GameDao gameDao;
	
	@Autowired
	private UserDao userDao;

	@Test
	public void testFindAll() {
		Iterator<Player> players = playerDao.findAll().iterator();
		assertTrue(players.hasNext());
	}
	
	@Test
	public void testFindByGame() {
		Game game = gameDao.findAll().iterator().next();
		
		List<Player> players = playerDao.findByGame(game);
		assertThat(players.size(), is(1));
		String playerUsername = players.get(0).getUser().getUsername();
		assertThat(playerUsername, is("bob"));
	}

	@Test
	public void testFindByUserOne() {
		User user = userDao.findOne("bob");
		List<Player> players = playerDao.findByUser(user);
		assertThat(players.size(), is(5));
	}

	@Test
	public void testFindByUserTwo() {
		User user = userDao.findOne("dave");
		List<Player> players = playerDao.findByUser(user);
		assertThat(players.size(), is(3));
	}

}
