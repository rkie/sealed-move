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
import org.junit.Ignore;
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
public class RegisterTest {

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
    @WithMockUser
    public void testLoggedInUserShouldNotSeeRegisterPage() throws Exception {
    	mockMvc.perform(get("/register"))
    		.andExpect(status().isOk())
    		.andExpect(content().string(containsString("Please log out before registering.")));
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
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "firstName")
    			.param("surname", "surname")
    			.param("email", "email@provider.com")
    			.param("password", "password")
    			.param("matchingPassword", "password"))
    		.andExpect(status().isFound())	// 302 redirect
    		.andExpect(redirectedUrl("/home"));
    }
    
    @Test
    public void testBlankFirstName() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.first.name", null, Locale.UK);
    	
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "")
    			.param("surname", "surname")
    			.param("email", "email@provider.com")
    			.param("password", "password")
    			.param("matchingPassword", "password"))
    		.andExpect(status().isOk())	// 200 but errors in body
    		.andExpect(content().string(containsString(errorMessage)));
    }
    
    @Test
    public void testBlankSurame() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.surname", null, Locale.UK);
    	
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "firstName")
    			.param("surname", "  ")
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
    			.param("surname", "surname")
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
    			.param("surname", "surname")
    			.param("email", "email invalid")
    			.param("password", "password")
    			.param("matchingPassword", "password"))
    		.andExpect(status().isOk())	// 200 but errors in body
    		.andExpect(content().string(containsString(errorMessage)));
    }
    
    @Test
    @Ignore("Implementation not complete")
    public void testEmailAlreadyExists() {
    	// TODO: Implement this test
    }
    
    @Test
    public void testUnmatchingPasswords() throws Exception {
    	String errorMessage = messageSource.getMessage("register.error.password.different", null, Locale.UK);
    	mockMvc.perform(post("/register")
    			.with(csrf())
    			.param("firstName", "firstName")
    			.param("surname", "surname")
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
    			.param("surname", "surname")
    			.param("email", "email@provider.com")
    			.param("password", "pass")
    			.param("matchingPassword", "pass"))
    		.andExpect(status().isOk())	// 200 but errors in body
    		.andExpect(content().string(containsString(errorMessage)));
    }

}
