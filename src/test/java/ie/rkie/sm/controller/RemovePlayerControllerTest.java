package ie.rkie.sm.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.Player;
import ie.rkie.sm.db.PlayerDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests both the controller and the template page that the caller is redirected to.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RemovePlayerControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    
    private String urlStructure; 
    
    @Before
    public void before() {
    	mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
    	urlStructure = "/removeplayer?gameid=%d&playOrder=%d";
    }
    
    @Autowired
    private GameDao gameDao;
    
    @Autowired
    private PlayerDao playerDao;

 	/**
 	 * Test not logged in is redirected
 	 * @throws Exception
 	 */
    @Test
    @WithAnonymousUser
    public void testGetNotLoggedIn() throws Exception {
    	String url  = String.format(urlStructure, 1, 1);
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/login"));
    }
	
	/**
	 * Test not the owner (is joiner) so not allowed
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="tom", userDetailsServiceBeanName="userDetailsService")
    public void testJoinerNotOwner() throws Exception {
    	String url  = String.format(urlStructure, 1, 1);
    	mockMvc.perform(get(url))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string(containsString("You cannot perform that action")));
    }
	
	/**
	 * Test not owner or joiner so no access
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    public void testNotOwnerOrJoinerToGame() throws Exception {
    	String url  = String.format(urlStructure, 1, 1);
    	mockMvc.perform(get(url))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string(containsString("You cannot perform that action")));
    }
	
	/**
	 * Test that the requested player does not exist
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testRemoveNonExistantPlayer() throws Exception {
		String url  = String.format(urlStructure, 1, 2);
		String expectedUrl = "/game?gameid=1";
		final String expectedMessage = "Attempt to remove non-existant player has failed";
		mockMvc.perform(get(url))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(expectedUrl))
			.andExpect(flash().attribute("changeStatus", "warning"))
			.andExpect(flash().attribute("changeMessage", expectedMessage));
		// load redirect with flash attributes
		mockMvc.perform(
				get(expectedUrl)
				.flashAttr("changeStatus", "warning")
				.flashAttr("changeMessage", expectedMessage))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(expectedMessage)));
    }
	
	/**
	 * Test remove player 1 from 3 player game - other players should reduce playorder down to 1 and 2
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    public void testRemovePlayer1From3PlayerGame() throws Exception {
    	final int gameId = 4;
		String url  = String.format(urlStructure, gameId, 1);
		String expectedUrl = "/game?gameid=" + gameId;
		String expectedFlashMessage = "Player has been removed";
		mockMvc.perform(get(url))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(expectedUrl))
			.andExpect(flash().attributeExists("changeStatus"))
			.andExpect(flash().attributeExists("changeMessage"))
			.andExpect(flash().attribute("changeStatus", is("success")))
			.andExpect(flash().attribute("changeMessage", is(expectedFlashMessage)));
		// check DB
		Game game = gameDao.getOne(gameId);
		final int remainingPlayers = game.getPlayers().size();
		assertThat(remainingPlayers, is(2));
		Player player = playerDao.findOneByGameAndPlayOrder(game, 1);
		assertThat("Player 2 should now be in position 1", player.getUser().getUsername(), is("bob"));
    }
	
	/**
	 * Test remove player 2 from 2 player game - player 1 should stay the same
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testRemovePlayer2From2PlayerGame() throws Exception {
    	final int gameId = 1;
		String url  = String.format(urlStructure, gameId, 2);
		String expectedUrl = "/game?gameid=" + gameId;
		String expectedFlashMessage = "Player has been removed";
		mockMvc.perform(get(url))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(expectedUrl))
			.andExpect(flash().attributeExists("changeStatus"))
			.andExpect(flash().attributeExists("changeMessage"))
			.andExpect(flash().attribute("changeStatus", is("success")))
			.andExpect(flash().attribute("changeMessage", is(expectedFlashMessage)));
		// check DB
		Game game = gameDao.getOne(gameId);
		final int remainingPlayers = game.getPlayers().size();
		assertThat(remainingPlayers, is(1));
		Player player = playerDao.findOneByGameAndPlayOrder(game, 1);
		assertThat("Original player should still be in position 1", player.getUser().getUsername(), is("dave"));
    }
	
	/** 
	 * Test remove only player in a game - no players afterwards
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testRemoveonlyPlayer() throws Exception {
    	final int gameId = 0;
		String url  = String.format(urlStructure, gameId, 1);
		String expectedUrl = "/game?gameid=" + gameId;
		String expectedFlashMessage = "Player has been removed";
		mockMvc.perform(get(url))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(expectedUrl))
			.andExpect(flash().attributeExists("changeStatus"))
			.andExpect(flash().attributeExists("changeMessage"))
			.andExpect(flash().attribute("changeStatus", is("success")))
			.andExpect(flash().attribute("changeMessage", is(expectedFlashMessage)));
		// check DB
		Game game = gameDao.getOne(gameId);
		final int remainingPlayers = game.getPlayers().size();
		assertThat(remainingPlayers, is(0));
    }
	
	/**
	 * Test finished game - can't remove players
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="tony", userDetailsServiceBeanName="userDetailsService")
    public void testRemoveFinishedGame() throws Exception {
		String url  = String.format(urlStructure, 2, 1);
		final String retest = "/game?gameid=2";
		mockMvc.perform(get(url))
			.andExpect(status().isUnauthorized());
		mockMvc.perform(get(retest))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("<td>tony</td>\n\t\t\t\t\t<td>1</td>")))
			.andExpect(content().string(containsString("<td>bob</td>\n\t\t\t\t\t<td>2</td>")))
			.andExpect(content().string(containsString("<td>dave</td>\n\t\t\t\t\t<td>3</td>")));
    }
	
	/**
	 * Test active game - can't remove players
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="tom", userDetailsServiceBeanName="userDetailsService")
	public void testAttemptToChangeActiveGame() throws Exception {
    	String url  = String.format(urlStructure, 3, 1);
		mockMvc.perform(get(url))
			.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/game?gameid=3"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("<td>tom</td>\n\t\t\t\t\t<td>1</td>")))
			.andExpect(content().string(containsString("<td>bob</td>\n\t\t\t\t\t<td>2</td>")));
    }
	
	/**
	 * Test invalid parameter
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testInvalidParameter() throws Exception {
		String url  = String.format(urlStructure, 1, 1) + "time";
    	mockMvc.perform(get(url))
			.andExpect(status().isBadRequest());
    }
	
	/**
	 * Test no parameters
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testNoParameters() throws Exception {
		String url  = "/removeplayer";
		mockMvc.perform(get(url))
			.andExpect(status().isBadRequest());
    }
	
	/**
	 * Game not found
	 * @throws Exception
	 */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testInvalidGame() throws Exception {
		String url  = String.format(urlStructure, 100, 1);
		mockMvc.perform(get(url))
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("Could not find that game")));
    }
    
    @After
    public void after() {
    	// TODO: Replace any successfully removed players!
    }

}
