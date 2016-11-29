package ie.rkie.sm.service;

import ie.rkie.sm.db.UserDao;
import ie.rkie.sm.dto.RegisterDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	private UserDao dao;
	
	@Override
	public void registerNewUser(RegisterDTO dto) {
		// TODO Complete this implementation
		System.out.println("Registering DTO: " + dto);
	}

	@Override
	public boolean emailExists(String email) {
		return dao.findByEmail(email).size() > 0;
	}

	@Override
	public boolean usernameExists(String username) {
		return dao.findByUsername(username).size() > 0;
	}

}
