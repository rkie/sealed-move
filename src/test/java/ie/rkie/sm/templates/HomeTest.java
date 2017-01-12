package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import javax.transaction.Transactional;

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
public class HomeTest {

	@Autowired
	private MessageSource messageSource;

    @Autowired
    private WebApplicationContext context;
    
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
    public void testNotLoggedInHome() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Welcome to the Sealed Move site")))
            .andExpect(content().string(containsString("<button type=\"submit\" class=\"btn btn-primary\">Login</button>")))
            .andExpect(content().string(not(containsString("Signed in as"))));
    }
    
    /**
     * Using {@link WithUserDetails} here takes the data from the test DB. This
     * means that the first name will be Robert and not just the username, bob.
     * @throws Exception
     */
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    @Transactional
    public void testLoggedInHome() throws Exception {
    	String expectedLogin = messageSource.getMessage("nav.bar.signed.in.as", null, Locale.UK);
    	String expectedTitle = messageSource.getMessage("nav.bar.signed.in.as", null, Locale.UK);
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(expectedTitle)))
            .andExpect(content().string(containsString(expectedLogin)))
            .andExpect(content().string(containsString("<span>Robert</span>")))
            .andExpect(content().string(not(containsString("<button type=\"submit\" class=\"btn btn-primary\">Login</button>"))));
    }

    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testJoinAndStartMenuItems() throws Exception {
    	String expectedStart = messageSource.getMessage("nav.bar.start", null, Locale.UK);
    	String expectedJoin = messageSource.getMessage("nav.bar.join", null, Locale.UK);
    	String expectedMyGames = messageSource.getMessage("nav.bar.my.games", null, Locale.UK);
        mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(expectedStart)))
        .andExpect(content().string(containsString(expectedJoin)))
        .andExpect(content().string(containsString(expectedMyGames)));
    }
}
