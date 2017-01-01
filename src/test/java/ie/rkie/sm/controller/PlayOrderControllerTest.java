package ie.rkie.sm.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class PlayOrderControllerTest {

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
    	urlStructure = "/changeorder?gameid=%d&playOrder=%d&direction=%s";
    }
    
    /**
     * Test anonymous access redirect
     * @throws Exception
     */
    @Test
    @WithAnonymousUser
    public void testGetNotLoggedIn() throws Exception {
    	String url  = String.format(urlStructure, 1, 1, "up");
    	System.out.println(url);
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/login"));
    }

    /**
     * Test moving a player from 1st to 2nd
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testMoveFrom1To2() throws Exception {
    	String url  = String.format(urlStructure, 1, 1, "up");
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/game/1"))
    		.andExpect(content().string(containsString("<td>dave</td>\n\t\t\t\t\t<td>2<\td>")));
    }
    
    /**
     * Test moving a player from 3rd to 2nd
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    public void testMoveFrom3To2() throws Exception {
    	String url  = String.format(urlStructure, 4, 3, "down");
    	System.out.println(url);
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/game/4"))
    		.andExpect(content().string(containsString("<td>dave</td>\n\t\t\t\t\t<td>2<\td>")));
    }
    
    // TODO Test moving a player from 2nd to 1st
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    public void testMoveFrom2To1() throws Exception {
    	String url  = String.format(urlStructure, 4, 2, "down");
    	System.out.println(url);
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/game?gameid="))
    		.andExpect(content().string(containsString("<td>dave</td>\n\t\t\t\t\t<td>1<\td>")));
    }
    
    // TODO Test don't have access to change (not joined or owning)
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    public void testNotOwnerOrJoinerToGame() throws Exception {
    	String url  = String.format(urlStructure, 1, 1, "up");
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/game?gameid=1"))
    		.andExpect(content().string(containsString("You do not have access to see this game.")));
    }
    
    // TODO Test don't have access to change (joined not owning)
    @Test
    @WithUserDetails(value="tom", userDetailsServiceBeanName="userDetailsService")
    public void testJoinerNotOwner() throws Exception {
    	String url  = String.format(urlStructure, 1, 1, "up");
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/error"))
    		.andExpect(content().string(containsString("You cannot perform that action")));
    }
    
    /**
     * Test changing a finished game.
     * @throws Exception
     */
    @Test
    @WithAnonymousUser
    public void testAttemptToChangeFinishedGame() throws Exception {
    	String url  = String.format(urlStructure, 3, 1, "up");
    	System.out.println(url);
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/game/3"))
    		.andExpect(content().string(containsString("<td>tony</td>\n\t\t\t\t\t<td>1<\td>")))
    		.andExpect(content().string(containsString("<td>bob</td>\n\t\t\t\t\t<td>2<\td>")))
    		.andExpect(content().string(containsString("<td>dave</td>\n\t\t\t\t\t<td>3<\td>")));
    }

}
