package ie.rkie.sm.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class RegisterDTO {

	@NotBlank
	@NotEmpty
	private String firstName;
	@NotEmpty
	@NotBlank
	private String surname;
	@NotEmpty
	@NotBlank
	private String email;
	private String password;
	private String matchingPassword;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMatchingPassword() {
		return matchingPassword;
	}

	public void setMatchingPassword(String matchingPassword) {
		this.matchingPassword = matchingPassword;
	}

	@Override
	public String toString() {
		return String.format(
				"RegisterDTO [firstName=%s, \nsurname=%s, \nemail=%s]",
				firstName, surname, email);
	}

}
