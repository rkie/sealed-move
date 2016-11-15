package ie.rkie.sm.controller;

import ie.rkie.sm.dto.RegisterDTO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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
	public ModelAndView register(@ModelAttribute("user") RegisterDTO registerDTO) {
		System.out.println(registerDTO.toString());
		
		return new ModelAndView();
	}

}
