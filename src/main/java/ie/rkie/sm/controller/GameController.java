package ie.rkie.sm.controller;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.User;
import ie.rkie.sm.dto.GameDTO;
import ie.rkie.sm.service.ListGamesService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to list and give details on the logged in user's games.
 *
 */
@Controller
@RequestMapping("/games")
public class GameController {
	
	@Autowired
	private ListGamesService service;

	/**
	 * Gets all active games for the logged in user for viewing via the template games.
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String games(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		// Get the active games
		List<Game> games = service.listActiveJoinedGames(user);
		List<GameDTO> gameDTOs = new ArrayList<GameDTO>();
		for (Game game : games ) {
			gameDTOs.add(createDTO(game));
		}
		// Get the games created
		List<Game> gamesCreated = service.listActiveOwnedGames(user);
		List<GameDTO> gameDTOCreated = new ArrayList<GameDTO>();
		for (Game game : gamesCreated ) {
			gameDTOCreated.add(createDTO(game));
		}
		// add to model
		model.addAttribute("games", gameDTOs);
		model.addAttribute("createdGames", gameDTOCreated);
		
		// return view
		return "games";
	}
	
	/**
	 * Helper to create a {@link GameDTO} from a {@link Game}.
	 * @param game
	 * @return
	 */
	private GameDTO createDTO(Game game) {
		GameDTO dto = new GameDTO(game.getGameType().getDisplayName());
		String playerList = game.getPlayers()
			.stream()
			.map(player -> player.getUser().getUsername())
			.collect(Collectors.joining(", "));
		dto.setPlayerList(playerList);
		dto.setStatus(game.getStatus());
		dto.setGameId(game.getGid());
		return dto;
	}
	
}
