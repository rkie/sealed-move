package ie.rkie.sm.service;

import ie.rkie.sm.db.User;
import ie.rkie.sm.db.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom {@link UserDetailsService} so that additional data can be
 * loaded when authenticating users, such as first name.
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user = userDao.findOne(username);
		if ( user == null ) {
			throw new UsernameNotFoundException("Username " + username + " not found");
		}
		return user;
	}

}
