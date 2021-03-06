package ie.rkie.sm.controller;

import ie.rkie.sm.dto.RegisterDTO;
import ie.rkie.sm.service.RegistrationService;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/register")
public class RegisterController {
	
	/**
	 * This is required so that the controller looks up validation messages for
	 * the appropriate locale. Also requires the initBinder below.
	 */
	@Autowired
	private Validator validator;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	  binder.setValidator(validator);
	}
	
	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@RequestMapping(method = RequestMethod.GET)
	public String registerForm(Model model) {
		RegisterDTO register = new RegisterDTO();
		model.addAttribute("register", register);
		
		return "register";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String register(@ModelAttribute("register") @Valid RegisterDTO registerDTO,
			Errors errors, BindingResult result) {
		if ( errors.hasErrors() ) {
			if ( errors.getGlobalErrors().size() > 0 ) {
				result.rejectValue("password", "register.error.password.different");
			}
			return "register";
		}
		
		if ( registrationService.emailExists(registerDTO.getEmail()) ) {
			result.rejectValue("email", "register.error.email.exists");
			return "register";
		}
		
		if ( registrationService.usernameExists(registerDTO.getUsername()) ) {
			result.rejectValue("username", "register.error.username.exists");
			return "register";
		}
		
		registrationService.registerNewUser(registerDTO);
		
		Authentication request = new UsernamePasswordAuthenticationToken(registerDTO.getUsername(), registerDTO.getPassword());
		Authentication auth = authenticationManager.authenticate(request);
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		return "redirect:/home";
	}
}
