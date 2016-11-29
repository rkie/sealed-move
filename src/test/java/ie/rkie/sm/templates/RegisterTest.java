package ie.rkie.sm.templates;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ie.rkie.sm.db.User;
import ie.rkie.sm.db.UserDao;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegisterTest {

    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private UserDao userDao;

    
    @Before
    public void before() {
    	mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
    }
    
    @Test
    @WithUserDetails(value="bob", userDetailsServiceBeanName="userDetailsService")
    public void testLoggedInUserShouldNotSeeRegisterPage() throws Exception {
    	mockMvc.perform(get("/register"))
    		.andExpect(status().isOk())
    		.andExpect(content().string(containsString("Please <a href=\"javascript: document.logoutForm.submit()\" role=\"menuitem\"> log out</a> before registering.")));
    }
    
    @Test
    @WithAnonymousUser
    public void testRegisterPage() throws Exception {
    	mockMvc.perform(get("/register"))
    		.andExpect(status().isOk())
    		.andExpect(content().string(containsString("<h1>Register</h1>")));
    }
    
    @Test
    public void testValidRegistration() throws Exception {
    	final String firstName = "firstName";
    	final String username = "username";
    	final String email = "email@provider.com";
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", firstName)
    			.param("username", username)
    			.param("email", email)
    			.param("password", "password")
    			.param("matchingPassword", "password"))
    		.andExpect(status().isFound())	// 302 redirect
    		.andExpect(redirectedUrl("/home"));
    	
    	// TODO: Add check to ensure user gets logged in. Does not seem possible to check because of redirect.
    	
    	// verify new user created
    	List<User> users = userDao.findByEmail(email);
    	assertEquals("Expected the user to be created", 1, users.size());
    	User user = users.get(0);
    	assertEquals("Wrong first name", firstName, user.getFirstName());
    	assertEquals("Wrong username", username, user.getUsername());
    	assertEquals("Wrong email", email, user.getEmail());
    	assertEquals("Should have 1 authority", 1, user.getAuthorities().size());
    	GrantedAuthority auth = user.getAuthorities().iterator().next();
    	assertEquals("Wrong authority", "ROLE_USER", auth.getAuthority());
    }
    
    @Test
    public void testBlankFirstName() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.first.name", null, Locale.UK);
    	
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "")
    			.param("username", "username")
    			.param("email", "email@provider.com")
    			.param("password", "password")
    			.param("matchingPassword", "password"))
    		.andExpect(status().isOk())	// 200 but errors in body
    		.andExpect(content().string(containsString(errorMessage)));
    }
    
    @Test
    public void testBlankSurame() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.username", null, Locale.UK);
    	
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "firstName")
    			.param("username", "  ")
    			.param("email", "email@provider.com")
    			.param("password", "password")
    			.param("matchingPassword", "password"))
    		.andExpect(status().isOk())	// 200 but errors in body
    		.andExpect(content().string(containsString(errorMessage)));
    }
    
    @Test
    public void testNullEmail() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.email", null, Locale.UK);
    	
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "firstName")
    			.param("username", "username")
    			.param("password", "password")
    			.param("matchingPassword", "password"))
    		.andExpect(status().isOk())	// 200 but errors in body
    		.andExpect(content().string(containsString(errorMessage)));
    }
    
    @Test
    public void testInvlaidEmail() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.email", null, Locale.UK);
    	
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "firstName")
    			.param("username", "username")
    			.param("email", "email invalid")
    			.param("password", "password")
    			.param("matchingPassword", "password"))
    		.andExpect(status().isOk())	// 200 but errors in body
    		.andExpect(content().string(containsString(errorMessage)));
    }
    
    @Test
    public void testEmailAlreadyExists() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.email.exists", null, Locale.UK);
    	mockMvc.perform(post("/register")
		    	.with(csrf())
				.param("firstName", "firstName")
				.param("username", "username")
				.param("email", "bob@test.com")
				.param("password", "password")
				.param("matchingPassword", "password"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(errorMessage)));
    }
    
    @Test
    public void testUsernameAlreadyExists() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.username.exists", null, Locale.UK);
    	mockMvc.perform(post("/register")
		    	.with(csrf())
				.param("firstName", "firstName")
				.param("username", "bob")
				.param("email", "valid@new.email")
				.param("password", "password")
				.param("matchingPassword", "password"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(errorMessage)));
    }
    
    @Test
    public void testUnmatchingPasswords() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.password.different", null, Locale.UK);
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "firstName")
    			.param("username", "username")
    			.param("email", "email@provider.com")
    			.param("password", "password")
    			.param("matchingPassword", "different password"))
    		.andExpect(status().isOk())	// 200 but errors in body
    		.andExpect(content().string(containsString(errorMessage)));
    }
    
    @Test
    public void testShortPasswords() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.password.short", null, Locale.UK);
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "firstName")
    			.param("username", "username")
    			.param("email", "email@provider.com")
    			.param("password", "pass")
    			.param("matchingPassword", "pass"))
    		.andExpect(status().isOk())	// 200 but errors in body
    		.andExpect(content().string(containsString(errorMessage)));
    }

}
