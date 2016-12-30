package ie.rkie.sm.db;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
		Player player = new Player();
		player.setGame(game);
		player.setPlayOrder(1);
		player.setUser(owner);
		game.getPlayers().add(player);
		
		// test
		Game saved = dao.save(game);
		
		assertThat(saved.getGid(), notNullValue());
		assertThat(saved.getPlayers().size(), is(1));
	}
	
	@Test
	@Transactional
	public void testPlayersJoin() {
		Game game = dao.findAll().iterator().next();
		List<Player> players = game.getPlayers();
		assertThat(players.size(), is(not(0)));
	}

	@Test
	public void testFindByOwner() {
		List<Game> games = dao.findByOwner(owner);
		System.out.println(games.size());
		assertThat(games, is(not(empty())));
		assertThat(games.size(), is(1));
	}

	@Test
	@Transactional
	public void testFindByOwnerUsernameOrPlayersUserUsername() {
		List<Game> games = dao.findByOwnerUsernameOrPlayersUserUsername("bob", "bob");
		assertThat(games.size(), is(5));
	}

	@Test
	public void testFindByOwnerUsernameOrPlayersUserUsernamePaged() {

		PageRequest pageable = new PageRequest(0, 2);
		Page<Game> pages = dao.findByOwnerUsernameOrPlayersUserUsername(pageable, "bob", "bob");
		assertThat(pages.getContent().size(), is(2));
	}

	@Test
	@Transactional
	public void  testOutputGameData() {
		List<Game> games = dao.findAll();
		StringBuilder b = new StringBuilder();
		for ( Game game : games ) {
			if ( b.length() > 0 ) {
				b.append("\n");
			}
			String gameType = game.getGameType().getDisplayName();
			String status = game.getStatus();
			String owner = game.getOwner().getUsername();
			String playerList = game.getPlayers()
					.stream()
					.map(player -> player.getUser().getUsername())
					.collect(Collectors.joining(", "));
			String line = String.format("|%1$-20s | %2$-30s | %3$-8s | %4$-10s |",
					gameType, playerList, status, owner);
			b.append(line);
		}
		System.out.println(b.toString());
	}
}
