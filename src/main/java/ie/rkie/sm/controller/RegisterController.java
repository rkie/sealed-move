package ie.rkie.sm.controller;

import ie.rkie.sm.dto.RegisterDTO;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/register")
public class RegisterController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String registerForm(Model model) {
		RegisterDTO register = new RegisterDTO();
		model.addAttribute("register", register);
		
		return "register";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String register(@ModelAttribute("register") @Valid RegisterDTO registerDTO,
			Errors errors) {
		if ( errors.hasErrors() ) {
			return "register";
		}
		return "redirect:/home";
	}
}
