package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class GamesTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    
    @Autowired
    private MessageSource messageSource;
    
    @Before
    public void before() {
    	mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
    }
    
    @Test
    @WithAnonymousUser
    public void testNotLoggedIn() throws Exception {
    	mockMvc.perform(get("/games"))
		.andExpect(status().isFound())
		.andExpect(redirectedUrl("http://localhost/login"));
    }
    
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testNoGameSpecified() throws Exception {
    	String expected = messageSource.getMessage("game.not.found", null, Locale.UK);
    	mockMvc.perform(get("/game"))
    		.andExpect(status().isNotFound())
    		.andExpect(content().string(
    				containsString(expected)));
    }
    
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testGamesListed() throws Exception {
    	String expected = messageSource.getMessage("games.active.header", null, Locale.UK);
		mockMvc.perform(get("/games").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(
				containsString(expected)));
    }

    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testNoCreatedGames() throws Exception {
    	String expected = messageSource.getMessage("games.active.joined", null, Locale.UK);
		mockMvc.perform(get("/games").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(
				containsString(expected)));
    }
    
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testNoJoinedGames() throws Exception {
    	String expected = messageSource.getMessage("games.active.created", null, Locale.UK);
		mockMvc.perform(get("/games").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(
				containsString(expected)));
    }
    
    @Test
    @WithUserDetails(value="ron", userDetailsServiceBeanName="userDetailsService")
    public void testNoGamesAtAll() throws Exception {
    	String expected = messageSource.getMessage("games.active.no.games", null, Locale.UK);
    	String notContains1 = messageSource.getMessage("games.active.joined", null, Locale.UK);
    	String notContains2 = messageSource.getMessage("games.active.created", null, Locale.UK);
		mockMvc.perform(get("/games").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString(expected)))
		.andExpect(content().string(not(containsString(notContains1))))
		.andExpect(content().string(not(containsString(notContains2))));

    }

}
