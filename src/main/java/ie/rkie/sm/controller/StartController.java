package ie.rkie.sm.controller;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameType;
import ie.rkie.sm.db.User;
import ie.rkie.sm.dto.GameSelectionDTO;
import ie.rkie.sm.service.GameService;
import ie.rkie.sm.service.LinkService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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

	@Autowired
	private LinkService linkService;

	@RequestMapping(method = RequestMethod.GET)
	public String startGame(Model model) {
		// Load from data base
		List<GameSelectionDTO> games = gameService.gameList();
		model.addAttribute("games", games);
		
		return "/start";
	}
	
	@RequestMapping(value="{name}/game", method = RequestMethod.GET)
	public String startGame(@PathVariable String name, Model model, HttpServletRequest request) {
		System.out.println("Game Started");
		return startGame(name, 2, model, request);
	}
	
	@RequestMapping(value="{name}/{players}/game", method = RequestMethod.GET)
	public String startGame(@PathVariable String name, @PathVariable int players, Model model, HttpServletRequest request) {
		GameType gameType = gameService.fromName(name);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User owner = (User) auth.getPrincipal();
		Game game = gameService.startGame(gameType, players, owner);
		String token = gameService.createToken(game);
		model.addAttribute("token", token);
		model.addAttribute("game", name);
		
		// Determine full url based on request
		String baseUrl = linkService.baseUrl(request.getLocalName(), request.getLocalPort());
		String url = baseUrl + "join?token=" + token;
		model.addAttribute("joinUrl", url);
		
		return "started";
	}
	
}
