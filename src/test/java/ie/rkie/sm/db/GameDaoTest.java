package ie.rkie.sm.db;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameDaoTest {
	
	@Autowired
	private GameDao dao;
	
	@Autowired
	private GameTypeDao gameTypeDao;
	
	@Autowired 
	private UserDao userDao;
	
	private GameType gameType;
	private User owner;
	
	@Before
	public void before() {
		gameType = gameTypeDao.findAll().iterator().next();
		owner = userDao.findAll().iterator().next();
	}
	
	@Test
	@Transactional
	public void testSave() {
		Game game = new Game();
		game.setGameType(gameType);
		game.setOwner(owner);
		game.setStatus("SETUP");
		
		// test
		Game saved = dao.save(game);
		
		assertThat(saved.getGid(), notNullValue());
	}

}
