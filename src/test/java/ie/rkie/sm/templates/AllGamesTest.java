package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
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
public class AllGamesTest {

    @Autowired
    private WebApplicationContext context;
    
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
    public void testNotLoggedIn() throws Exception {
    	mockMvc.perform(get("/allgames/0"))
		.andExpect(status().isFound())
		.andExpect(redirectedUrl("http://localhost/login"));
    }
    
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testGamesListed() throws Exception {
    	final String expected = messageSource.getMessage("game.all.header", null, Locale.UK);
		mockMvc.perform(get("/allgames/0").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(
				containsString(expected)));
    }
    
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testTwoPagesReturned() throws Exception {
		mockMvc.perform(get("/allgames/0").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("<a href=\"/allgames/1\">Next</a>")));
    }
    
    @Test
    @WithUserDetails(value="ron", userDetailsServiceBeanName="userDetailsService")
    public void testNoGamesAtAll() throws Exception {
    	final String expected = messageSource.getMessage("game.all.no.games.header", null, Locale.UK);
		mockMvc.perform(get("/allgames/0").with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(
				containsString(expected)));
    }
}
