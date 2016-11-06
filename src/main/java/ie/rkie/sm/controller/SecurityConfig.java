package ie.rkie.sm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
            .antMatchers("/", "/home").permitAll()
            .antMatchers("/start").hasRole("USER")
            .antMatchers("/list").hasRole("USER")
            .antMatchers("/resources/css/**", "/index").permitAll()
            .antMatchers("/resources/fonts/**", "/index").permitAll()
            .antMatchers("/resources/js/**", "/index").permitAll()
            .anyRequest().authenticated()
            .and()
         // use the default spring login - note no way to log out yet 
        .formLogin()
//            .loginPage("/login")
//            .permitAll()
//            .and()
//        .logout()
//            .permitAll()
            ;

	}

	/**
	 * Temporary in memory fixed users and roles
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.inMemoryAuthentication()
				.withUser("user").password("password").roles("USER");
	}
}
