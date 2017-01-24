package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.UserDao;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SetupTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    
    @Before
    public void before() {
    	mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
    	
    	Game game = gameDao.findByOwner(userDao.findOne("bob")).get(0);
    	url = "/game?gameid=" + game.getGid();
		game = gameDao.findByOwnerUsername("mike").get(0);
		urlGameWithMinPlayers = "/game?gameid=" + game.getGid();
    }
    
    @Autowired
    private UserDao userDao;
   
    @Autowired
    private GameDao gameDao;
    
    @Autowired
    private MessageSource messageSource;
    
    private String url;
    
    private String urlGameWithMinPlayers;

    @Test
    @WithAnonymousUser
    public void testGetNotLoggedIn() throws Exception {
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/login"));
    }
    
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
	public void testGetGameThatIsSettingUp() throws Exception {
		mockMvc.perform(get(url).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("<h1>This game of <span>Chess</span> still setting up.</h1>")));
	}
    
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
	public void testGetGameButNotAllowed() throws Exception {
    	final String expectedMessage = messageSource.getMessage("game.no.access", null, Locale.UK);
		mockMvc.perform(get(url).with(csrf()))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string(containsString(expectedMessage)));
	}

    /**
	 * Test for a game that has not reached the minimum number of players.
	 * Should have join tokens visible but not start option
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testMinPlayersNotReached() throws Exception {
		mockMvc.perform(get(url).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("UNIQUE_GAME_ENTRY_TOKEN")))
		.andExpect(content().string(not(containsString("Start Game"))));
    }

    /**
	 * Test for owner of game with minimum players - should be able to start the
	 * game, should be able to boot a player, change player order
     * @throws Exception
	 */
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    public void testOwnerOfGameWithMinPlayers() throws Exception {
		mockMvc.perform(get(urlGameWithMinPlayers).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("UNIQUE_TOKEN_GAME_MIN_REACHED")))
		.andExpect(content().string(containsString("Remove this player")))
		.andExpect(content().string(containsString("Start Game")));
    }

    /**
	 * Test for player in game with min players - no option to boot or change
	 * play order or start.
     * @throws Exception
	 */
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testJoinerOfGamewithMinPlayers() throws Exception {
		mockMvc.perform(get(urlGameWithMinPlayers).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("Snakes and Ladders")))
		.andExpect(content().string(not(containsString("UNIQUE_TOKEN_GAME_MIN_REACHED"))))
		.andExpect(content().string(not(containsString("Remove this player"))))
		.andExpect(content().string(not(containsString("Start Game"))));
    }

    /**
     *  Test for owner that has not joined own game yet
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    public void testOwnerHasNotJoinedYet() throws Exception {
		mockMvc.perform(get(urlGameWithMinPlayers).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("<h3>You have not joined you own game yet</h3>")));
    }

    // TODO: Test successful change message

    // TODO: Test warning of failed change message
}
