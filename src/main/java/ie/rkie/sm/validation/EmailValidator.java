package ie.rkie.sm.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * From http://www.baeldung.com/registration-with-spring-mvc-and-spring-security
 *
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

	private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" + 
    		"[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
    
	@Override
	public void initialize(ValidEmail email) {
		pattern = Pattern.compile(EMAIL_PATTERN);
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		if ( email == null ) {
			return false;
		}
        matcher = pattern.matcher(email);
        return matcher.matches();
	}

}
