package ie.rkie.sm.service;

import ie.rkie.sm.dto.RegisterDTO;

/**
 * Business rules service interface for registering users.
 *
 */
public interface RegistrationService {

	/**
	 * Register a new user with the default role of USER.
	 * @param dto
	 */
	void registerNewUser(RegisterDTO dto);
	
	/**
	 * Convenience method to verify the email is not already used.
	 * @param email
	 * @return
	 */
	boolean emailExists(String email);
	
	/**
	 * Convenience method to verify the username is not already used.
	 * @param username
	 * @return
	 */
	boolean usernameExists(String username);
			
}
