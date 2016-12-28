package ie.rkie.sm.service;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.User;
import ie.rkie.sm.db.UserDao;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ListGamesServiceTest {
	
	@Autowired
	private ListGamesService service;
	
	@Autowired
	private UserDao userDao;
	
	private User user;
	
	@Before
	public void before() {
		user = userDao.findOne("dave");
	}
	
	@Test
	public void testListActiveOwnedGames() {
		
		List<Game> games = service.listActiveOwnedGames(user);
		assertThat(games, is(not(empty())));
		assertThat(games.size(), is(1));
	}
	
	@Test
	public void testListActiveJoinedGames() {
		
		List<Game> games = service.listActiveJoinedGames(user);
		assertThat(games, is(not(empty())));
		assertThat(games.size(), is(1));
	}
	
	@Test
	public void testListFinishedOwnedGames() {
		user = userDao.findOne("tony");
		List<Game> games = service.listFinishedOwnedGames(user);
		assertThat(games, is(not(empty())));
		assertThat(games.size(), is(1));
	}
	
	@Test
	public void testListFinishedJoinedGames() {
		user = userDao.findOne("tony");
		List<Game> games = service.listFinishedJoinedGames(user);
		assertThat(games, is(not(empty())));
		assertThat(games.size(), is(1));
	}
	
	@Test
	public void testL() {
		user = userDao.findOne("bob");
		Page<Game> page = service.listAllGames(user, 0);
		assertThat(page.getContent().size(), is(2));
	}

}
