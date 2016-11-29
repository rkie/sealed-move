package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

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
	public void testLoginGet() throws Exception {    	
		mockMvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("input type=\"submit\" value=\"Log in\" class=\"btn btn-primary\"")));
	}
	
	@Test
	public void testValidLogin() throws Exception {
		mockMvc.perform(post("/login")
    			.with(csrf())
    			.param("username", "bob@test.com")
    			.param("password", "bobspassword"))
    		.andExpect(status().isFound())	// 302 redirect
    		.andExpect(redirectedUrl("/"));
    }
	
	@Test
	public void testInvalidLogin() throws Exception {
		mockMvc.perform(post("/login")
    			.with(csrf())
    			.param("username", "bob@test.com")
    			.param("password", "wrong password"))
    		.andExpect(status().isFound())	// 302 redirect
    		.andExpect(redirectedUrl("/login?error"));
	}	
	
	@Test
	@WithAnonymousUser
	public void testLoginError() throws Exception {
    	String errorMessage = messageSource.getMessage("login.error", null, Locale.UK);
		mockMvc.perform(get("/login?error"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(errorMessage)));
	}
	
	@Test
	@WithMockUser
	public void testLoginAlreadyLoggedIn() throws Exception {
		mockMvc.perform(get("/login"))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("Please <a href=\"javascript: document.logoutForm.submit()\"> log out</a> before changing account.")));
	}
}
