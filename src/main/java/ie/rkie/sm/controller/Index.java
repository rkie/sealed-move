package ie.rkie.sm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/index")
public class Index {
	
	@RequestMapping(method = RequestMethod.GET)
	public String welcom(Model model) {
		return "index";
	}

}
