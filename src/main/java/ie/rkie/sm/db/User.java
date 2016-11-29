package ie.rkie.sm.db;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Represents a player that can start or participate in a game. Also contains
 * security info used by Spring to authenticate the user.
 *
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {
	
	private static final long serialVersionUID = -6447999112721028520L;

	// The user's email
	@NotNull
	private String email;
	
	// The user's name
	@Id
	@NotNull
	private String username;
	
	@NotNull 
	private String firstName;
	
	private boolean enabled;
	
	@NotNull
	private String password;
	
	@OneToMany(mappedBy="username", fetch=FetchType.EAGER)
	private Collection<Authority> authorities;

	public User() {
		super();
	}

	public User(String email, String username) {
		super();
		this.email = email;
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"User [email=%s, \nusername=%s, \nfirstName=%s, \nenabled=%s]",
				email, username, firstName, enabled);
	}
	
}
