package ie.rkie.sm.controller;

import ie.rkie.sm.service.UserDetailsServiceImpl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/", "/home").permitAll()
				.antMatchers("/start").hasRole("USER")
				.antMatchers("/list").hasRole("USER")
				.antMatchers("/register").permitAll()
				.antMatchers("/resources/css/**", "/index").permitAll()
				.antMatchers("/resources/fonts/**", "/index").permitAll()
				.antMatchers("/resources/js/**", "/index").permitAll()
				.anyRequest().authenticated()
				.and()
				// use the default spring login - note no way to log out yet 
				.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
				.logout()
				.permitAll()
				.logoutSuccessUrl("/")
		;

	}
	
	/**
	 * Custom implementation to all additional fields from DB.
	 */
	@Bean(name="userDetailsService")
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	/**
	 * Use BCrypt random salting of password hashes.
	 * @return
	 */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	/**
	 * Temporary in memory fixed users and roles
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService())
			.passwordEncoder(passwordEncoder());
	}
}
