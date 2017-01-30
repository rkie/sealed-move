package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.UserDao;

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

/**
 * Tests the ready.html template.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReadyTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    
    @Before
    public void before() {
    	mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
    	
    	Game game = gameDao.findByOwner(userDao.findOne("dave")).get(0);
    	url = "/game?gameid=" + game.getGid();
    }
    
    @Autowired
    private UserDao userDao;
   
    @Autowired
    private GameDao gameDao;
    
    @Autowired
    private MessageSource messageSource; 
    
    private String url;
    
    @Test
    @WithAnonymousUser
    public void testGetNotLoggedIn() throws Exception {
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/login"));
    }
    
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
	public void testGetGameThatIsReady() throws Exception {
    	final String expectedReady = messageSource.getMessage("game.ready.can.start", null, Locale.UK);
    	final String expectedplayers = messageSource.getMessage("game.ready.player.list", null, Locale.UK);
    	final String expectedStartGame = messageSource.getMessage("game.start.game", null, Locale.UK);
    	
		mockMvc.perform(get(url).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString(expectedReady)))
		.andExpect(content().string(containsString(expectedplayers)))
		.andExpect(content().string(containsString(expectedStartGame)));
	}
    
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
	public void testGetGameThatIsReadyAsPlayer() throws Exception {
		mockMvc.perform(get(url).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("This game is ready to start")))
		.andExpect(content().string(containsString("Here is the list of registered players")))
		.andExpect(content().string(containsString("The owner of the game must start the game.")));
	}
    
    @Test
    @WithUserDetails(value="tony", userDetailsServiceBeanName="userDetailsService")
	public void testGetGameButNotAllowed() throws Exception {
		mockMvc.perform(get(url).with(csrf()))
		.andExpect(status().isUnauthorized())
		.andExpect(content().string(containsString("You do not have access to see this game.")));
	}

    // TODO: Successful message of a change

    // TODO: Warning message following a failed change

}
