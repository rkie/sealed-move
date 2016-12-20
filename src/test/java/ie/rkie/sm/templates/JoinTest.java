package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.JoinTokenDao;
import ie.rkie.sm.db.Player;

import java.util.List;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JoinTest {

    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private JoinTokenDao joinTokenDao;

    private MockMvc mockMvc;
    
    @Before
    public void before() {
    	mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
    }
    
    @Test
    @WithAnonymousUser
    public void testGetNotLoggedIn() throws Exception {
    	mockMvc.perform(get("/join"))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
	public void testGetWithoutToken() throws Exception {
		mockMvc.perform(get("/join").with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string(
					containsString("You can join a game by entering the game token here.")));
			
	}
    
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    @Transactional
	public void testGetWithToken() throws Exception {
    	final String token = "UNIQUE_GAME_ENTRY_TOKEN";
		mockMvc.perform(get("/join?token=" + token).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("You have successfully joined the game.")));
		// verify game was joined
		Game game = joinTokenDao.findByToken(token).get(0).getGame();
		List<Player> players = game.getPlayers();
		assertThat(players.size(), is(2));
	}

    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
	public void testGetWithInvalidToken() throws Exception {
		mockMvc.perform(get("/join?token=testtoken").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("You cannot join a game with the token provided.")));
	}

    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testAlreadyJoined() throws Exception {
		mockMvc.perform(get("/join?token=UNIQUE_GAME_ENTRY_TOKEN").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("You have joined the game associated with that token already.")));
    }

    @Test
    @WithUserDetails(value="tony", userDetailsServiceBeanName="userDetailsService")
    public void testPlayerLimitReached() throws Exception {
		mockMvc.perform(get("/join?token=UNIQUE_TOKEN_GAME_FULL").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("There are no more players allowed in the game associated with that token")));
    }
	
}
