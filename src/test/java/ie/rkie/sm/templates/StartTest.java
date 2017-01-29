package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.JoinToken;
import ie.rkie.sm.db.JoinTokenDao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StartTest {

    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private JoinTokenDao joinTokenDao;
    
    @Before
    public void before() {
    	mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
    }

	@Test
	@WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
	public void testStartGet() throws Exception {
		mockMvc.perform(get("/start"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("<a data-toggle=\"tab\" href=\"#chess\">Chess</a>")))
			.andExpect(content().string(containsString("<a data-toggle=\"tab\" href=\"#draughts\">Draughts</a>")))
			.andExpect(content().string(containsString("<a data-toggle=\"tab\" href=\"#snakes\">Snakes and Ladders</a>")));
	}
	
	@Test
	@WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
	public void testStartChessGame() throws Exception {
		MvcResult result = mockMvc.perform(get("/start/chess/game"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("You have started a game of chess!")))
			.andExpect(content().string(containsString("Send this joining link on to your opponent")))
			.andReturn();
		
		// check for the token
		String html = result.getResponse().getContentAsString();
		Pattern pattern = Pattern.compile(".*<div class=\"panel-body\"><span>.*token=(.*)<\\/span><\\/div>.*");
		Matcher matcher = pattern.matcher(html);
		assertTrue("Should have found token in response", matcher.find());
		String token = matcher.group(1);
		
		// verify the token is in the database
		List<JoinToken> tokens = joinTokenDao.findByToken(token);
		assertThat(token + " not found", tokens, hasSize(1));
		Game game = tokens.get(0).getGame();
		assertThat(game, notNullValue());
	}
}
