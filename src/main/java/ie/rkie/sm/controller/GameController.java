package ie.rkie.sm.controller;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.Player;
import ie.rkie.sm.db.User;
import ie.rkie.sm.dto.GameDTO;
import ie.rkie.sm.service.ListGamesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller to list and give details on the logged in user's games.
 *
 */
@Controller
public class GameController {
	
	@Autowired
	private ListGamesService service;

	@Autowired
	private GameDao gameDao;

	/**
	 * Gets all active games for the logged in user for viewing via the template games.
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path="/games")
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
		dto.setUrl("game?gameid=" + game.getGid());
		return dto;
	}
	
	@RequestMapping(path="/game", method = RequestMethod.GET)
	public String viewGame(@RequestParam(value="gameid", required=false) Integer gid,
			final HttpServletResponse response,
			Model model) throws IOException {
		Game game = gameDao.findOne(gid);
		if ( game == null ) {
			String message = "Could not find that game.";
			response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
			model.addAttribute("message", message);
			// the return to setup will not work - white label error page will take over
			return "error";
		}
		// TODO: this needs a better place and some finesse
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		if ( ! game.getOwner().getUsername().equals(user.getUsername()) ) {
			boolean found = false;
			for ( Player player : game.getPlayers() ) {
				if ( player.getUser().getUsername().equals(user.getUsername()) ) {
					found = true;
					break;
				}
			}
			if ( ! found ) {
				String message = "You do not have access to see this game.";
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
				model.addAttribute("message", message);
				return "error";
			}
		}
		if ( "SETUP".equals(game.getStatus()) ) {
			return "setup";
		}
		if ( "READY".equals(game.getStatus()) ) {
			return "ready";
		}
		if ( "ACTIVE".equals(game.getStatus()) ) {
			return "active";
		}
		// safe to return finished as fall-back as no action available in that template
		return "finished";
	}

	@RequestMapping(method = RequestMethod.GET, path="/allgames/{pageNumber}")
	public String allGames(@PathVariable Integer pageNumber, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		Page<Game> page = service.listAllGames(user, pageNumber);

		List<GameDTO> games = page.getContent().stream()
			.map(game -> createDTO(game))
			.collect(Collectors.toList());

		model.addAttribute("games", games);
		model.addAttribute("page", page);
		model.addAttribute("pageNo", pageNumber);
		return "allgames";
	}

}
