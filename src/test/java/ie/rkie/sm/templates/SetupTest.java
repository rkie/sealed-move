package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.UserDao;

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
    }
    
    @Autowired
    private UserDao userDao;
   
    @Autowired
    private GameDao gameDao;
    
    private String url;
    
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
		.andExpect(content().string(containsString("This game is setting up.")));
	}
    
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
	public void testGetGameButNotAllowed() throws Exception {
		mockMvc.perform(get(url).with(csrf()))
		.andExpect(status().isUnauthorized())
		.andExpect(content().string(containsString("You do not have access to see this game.")));
	}

}
