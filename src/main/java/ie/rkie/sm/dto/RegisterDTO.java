package ie.rkie.sm.dto;

import ie.rkie.sm.validation.PasswordMatches;
import ie.rkie.sm.validation.ValidEmail;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@PasswordMatches
public class RegisterDTO {

	@NotBlank(message="{register.error.first.name}")
	@NotEmpty(message="{register.error.first.name}")
	private String firstName;
	@NotEmpty(message="{register.error.surname}")
	@NotBlank(message="{register.error.surname}")
	private String surname;
	@NotEmpty(message="{register.error.email}")
	@NotBlank(message="{register.error.email}")
	@ValidEmail
	private String email;
	@NotEmpty(message="{register.error.password.invalid}")
	@NotBlank(message="{register.error.password.invalid}")
	@Size(min=5, message="{register.error.password.short}")
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
