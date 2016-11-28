package ie.rkie.sm.validation;

import ie.rkie.sm.dto.RegisterDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements
		ConstraintValidator<PasswordMatches, Object> {

	@Override
	public void initialize(PasswordMatches arg0) {
		
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext arg1) {
		RegisterDTO dto = (RegisterDTO) object;
		return dto.getPassword().equals(dto.getMatchingPassword());
	}

}
