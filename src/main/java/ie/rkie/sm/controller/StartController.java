package ie.rkie.sm.controller;

import ie.rkie.sm.dto.GameSelectionDTO;

import java.util.Arrays;
import java.util.List;

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
	

	@RequestMapping(method = RequestMethod.GET)
	public String startGame(Model model) {
		// TODO: Load from data base
		GameSelectionDTO chess = new GameSelectionDTO("chess", "Chess", 2, 2);
		GameSelectionDTO draughts = new GameSelectionDTO("draughts", "Draughts", 2, 2);
		GameSelectionDTO snakesAndLadders = new GameSelectionDTO("snakes", "Snakes and Ladders", 2, 4);
		List<GameSelectionDTO> games = Arrays.asList(chess, draughts, snakesAndLadders);
		model.addAttribute("games", games);
		
		return "/start";
	}
	
}
