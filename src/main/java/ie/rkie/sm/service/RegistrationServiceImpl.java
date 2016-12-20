package ie.rkie.sm.service;

import ie.rkie.sm.db.Authority;
import ie.rkie.sm.db.User;
import ie.rkie.sm.db.UserDao;
import ie.rkie.sm.dto.RegisterDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	private UserDao dao;
	
	@Override
	public void registerNewUser(RegisterDTO dto) {
		User user = new User();
		user.setEmail(dto.getEmail());
		user.setFirstName(dto.getFirstName());
		user.setEnabled(true);
		user.setUsername(dto.getUsername());
		user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
		Authority auth = new Authority();
		auth.setAuthority("ROLE_USER");
		auth.setUsername(dto.getUsername());
		user.addAuthority(auth);
		dao.save(user);
	}

	@Override
	public boolean emailExists(String email) {
		return dao.findByEmail(email).size() > 0;
	}

	@Override
	public boolean usernameExists(String username) {
		return dao.findOne(username) != null;
	}

}
