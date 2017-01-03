package ie.rkie.sm.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

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
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
    		.andExpect(redirectedUrl("http://localhost/login"));
    }

    /**
     * Test moving a player from 1st to 2nd. Note using transactional annotation so
     * that the data does not get committed and corrupt other tests.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    @Transactional
    public void testMoveFrom1To2() throws Exception {
    	final String url  = String.format(urlStructure, 4, 1, "up");
    	final String expectedRedirect = "/game?gameid=4";
		final String expectedFlashMessage = "Position successfully changed";
    	mockMvc.perform(get(url))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(expectedRedirect))
			.andExpect(flash().attributeExists("changeStatus"))
			.andExpect(flash().attributeExists("changeMessage"))
			.andExpect(flash().attribute("changeStatus", is("success")))
			.andExpect(flash().attribute("changeMessage", is(expectedFlashMessage)));
		mockMvc.perform(
				get(expectedRedirect)
				.flashAttr("changeStatus", "success")
				.flashAttr("changeMessage", expectedFlashMessage))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("<td>bob</td>\n\t\t\t\t\t<td>1</td>")))
			.andExpect(content().string(containsString("<td>tony</td>\n\t\t\t\t\t<td>2</td>")))
			.andExpect(content().string(containsString("<td>dave</td>\n\t\t\t\t\t<td>3</td>")))
			.andExpect(content().string(containsString(expectedFlashMessage)));
    }
    
    /**
     * Test moving a player from 3rd to 2nd. In transaction for auto-rollback.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    @Transactional
    public void testMoveFrom3To2() throws Exception {
    	String url  = String.format(urlStructure, 4, 3, "down");
		final String expectedRedirect = "/game?gameid=4";
		final String expectedFlashMessage = "Position successfully changed";
    	mockMvc.perform(get(url))
    		.andExpect(status().isFound())
			.andExpect(redirectedUrl(expectedRedirect))
			.andExpect(flash().attributeExists("changeStatus"))
			.andExpect(flash().attributeExists("changeMessage"))
			.andExpect(flash().attribute("changeStatus", is("success")))
			.andExpect(flash().attribute("changeMessage", is(expectedFlashMessage)));
		mockMvc.perform(
				get(expectedRedirect)
				.flashAttr("changeStatus", "success")
				.flashAttr("changeMessage", expectedFlashMessage))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("<td>tony</td>\n\t\t\t\t\t<td>1</td>")))
			.andExpect(content().string(containsString("<td>dave</td>\n\t\t\t\t\t<td>2</td>")))
			.andExpect(content().string(containsString("<td>bob</td>\n\t\t\t\t\t<td>3</td>")))
			.andExpect(content().string(containsString(expectedFlashMessage)));
    }
    
    /**
     * Test moving a player from 2nd to 1st. In transaction for auto-rollback.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    @Transactional
    public void testMoveFrom2To1() throws Exception {
    	String url  = String.format(urlStructure, 1, 2, "down");
		String expectedRedirect = "/game?gameid=1";
		final String expectedFlashMessage = "Position successfully changed";
		mockMvc.perform(get(url))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(expectedRedirect))
			.andExpect(flash().attributeExists("changeStatus"))
			.andExpect(flash().attributeExists("changeMessage"))
			.andExpect(flash().attribute("changeStatus", is("success")))
			.andExpect(flash().attribute("changeMessage", is(expectedFlashMessage)));
		mockMvc.perform(
				get(expectedRedirect)
				.flashAttr("changeStatus", "success")
				.flashAttr("changeMessage", expectedFlashMessage))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("<td>bob</td>\n\t\t\t\t\t<td>1</td>")))
			.andExpect(content().string(containsString("<td>dave</td>\n\t\t\t\t\t<td>2</td>")))
			.andExpect(content().string(containsString(expectedFlashMessage)));
    }
    
    /**
     * Test don't have access to change (not joined or owning).
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="mike", userDetailsServiceBeanName="userDetailsService")
    public void testNotOwnerOrJoinerToGame() throws Exception {
    	String url  = String.format(urlStructure, 1, 1, "up");
    	mockMvc.perform(get(url))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string(containsString("You cannot perform that action")));
    }
    
    /**
     * Test don't have access to change (joined not owning).
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="tom", userDetailsServiceBeanName="userDetailsService")
    public void testJoinerNotOwner() throws Exception {
    	String url  = String.format(urlStructure, 1, 1, "up");
    	mockMvc.perform(get(url))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string(containsString("You cannot perform that action")));
    }
    
    /**
     * Test changing a finished game.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="tony", userDetailsServiceBeanName="userDetailsService")
    public void testAttemptToChangeFinishedGame() throws Exception {
		String url  = String.format(urlStructure, 2, 1, "up");
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
     * Test changing an in progress game.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="tom", userDetailsServiceBeanName="userDetailsService")
	public void testAttemptToChangeActiveGame() throws Exception {
    	String url  = String.format(urlStructure, 3, 1, "up");
		mockMvc.perform(get(url))
			.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/game?gameid=3"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("<td>tom</td>\n\t\t\t\t\t<td>1</td>")))
			.andExpect(content().string(containsString("<td>bob</td>\n\t\t\t\t\t<td>2</td>")));
    }

    /**
     * Invalid parameters
     */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testInvalidParameter() throws Exception {
		String url  = String.format(urlStructure, 1, 1, "backwards");
    	mockMvc.perform(get(url))
			.andExpect(status().isBadRequest());
    }

    /**
     * No parameters
     */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testNoParameters() throws Exception {
		String url  = "/changeorder";
		mockMvc.perform(get(url))
			.andExpect(status().isBadRequest());
    }

    /**
     * Invalid Game
     */
    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testInvalidGame() throws Exception {
		String url  = String.format(urlStructure, 100, 1, "backwards");
		mockMvc.perform(get(url))
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("Could not find that game")));
    }

    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testInvalidToMovePlayerOneDown() throws Exception {
		String url  = String.format(urlStructure, 1, 1, "down");
		String expectedUrl = "/game?gameid=1";
		final String expectedMessage = "Request to move a player outide the possible range";
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

    @Test
    @WithUserDetails(value="dave", userDetailsServiceBeanName="userDetailsService")
    public void testInvalidToMoveTopPlayerUp() throws Exception {
		String url  = String.format(urlStructure, 1, 2, "up");
		String expectedUrl = "/game?gameid=1";
		final String expectedMessage = "Request to move a player outide the possible range";
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

}
