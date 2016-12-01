package ie.rkie.sm.controller;

import ie.rkie.sm.dto.GameSelectionDTO;
import ie.rkie.sm.service.GameService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Presents a list of games that can be started. If there is an optional number
 * of players, that can be adjusted. Once submitted, it provides a link that 
 * can be sent to other players.
 *
 */
@Controller
@RequestMapping(value="/start")
public class StartController {
	
	@Autowired
	private GameService gameService;

	@RequestMapping(method = RequestMethod.GET)
	public String startGame(Model model) {
		// Load from data base
		List<GameSelectionDTO> games = gameService.gameList();
		model.addAttribute("games", games);
		
		return "/start";
	}
	
}
