package ie.rkie.sm.controller;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.User;
import ie.rkie.sm.service.GameService;
import ie.rkie.sm.service.JoinAttemptResult;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The join page allows a player to join a game using the game's unique token.
 */
@Controller
@RequestMapping("/join")
public class JoinController {
	
	@Autowired
	private GameService gameService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String joinPageGet(@RequestParam(value="token", required=false) String token,
			Model model,
			Authentication auth,
			RedirectAttributes redirectAttributes) {
		if ( token == null ) {
			return "join";
		}
		else if ( StringUtils.isEmpty(token.trim()) ) {
			// use this to clear the url of empty tokens
			return "redirect:/join";
		}
		
		// get the current user
		User owner = (User) auth.getPrincipal();
		JoinAttemptResult result = gameService.joinWithToken(owner, token);
		if ( result == JoinAttemptResult.SUCCESS ) {
			Game game = gameService.from(token);
			redirectAttributes.addFlashAttribute("changeStatus", "success");
			redirectAttributes.addFlashAttribute("changeMessage", "You have successfully joined the game.");
			return "redirect:/game?gameid=" + game.getGid();
		}
		// TODO: Figure out the correct message to return if not successful
		String message = result.getMessage();
		model.addAttribute("errorMessage", message);
		return "join";
	}

}
