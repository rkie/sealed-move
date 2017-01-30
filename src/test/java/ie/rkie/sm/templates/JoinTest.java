package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.JoinTokenDao;
import ie.rkie.sm.db.Player;

import java.util.List;
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
    
    @Autowired
    private MessageSource messageSource;

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
    	final String expected = messageSource.getMessage("join.message", null, Locale.UK);
		mockMvc.perform(get("/join").with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string(
					containsString(expected)));
			
	}
    
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    @Transactional
	public void testGetWithToken() throws Exception {
    	final String expected = messageSource.getMessage("join.success", null, Locale.UK);
    	final String token = "UNIQUE_GAME_ENTRY_TOKEN";
		mockMvc.perform(get("/join?token=" + token).with(csrf()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/game?gameid=0"))
			.andExpect(flash().attribute("changeStatus", "success"))
			.andExpect(flash().attribute("changeMessage", expected));
		// verify game was joined
		Game game = joinTokenDao.findByToken(token).get(0).getGame();
		List<Player> players = game.getPlayers();
		assertThat(players.size(), is(2));
	}

    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
	public void testGetWithInvalidToken() throws Exception {
    	final String expected = messageSource.getMessage("join.token.not.found", null, Locale.UK);
		mockMvc.perform(get("/join?token=testtoken").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString(expected)));
	}

    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testAlreadyJoined() throws Exception {
    	final String expected = messageSource.getMessage("join.player.already.joined", null, Locale.UK);
		mockMvc.perform(get("/join?token=UNIQUE_GAME_ENTRY_TOKEN").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString(expected)));
    }

    @Test
    @WithUserDetails(value="tony", userDetailsServiceBeanName="userDetailsService")
    public void testPlayerLimitReached() throws Exception {
    	final String expected = messageSource.getMessage("join.player.limit.reached", null, Locale.UK);
		mockMvc.perform(get("/join?token=UNIQUE_TOKEN_GAME_FULL").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString(expected)));
    }
	
}
