package ie.rkie.sm.controller;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.JoinTokenDao;
import ie.rkie.sm.db.Player;
import ie.rkie.sm.db.User;
import ie.rkie.sm.dto.GameDTO;
import ie.rkie.sm.service.GameService;
import ie.rkie.sm.service.LinkService;
import ie.rkie.sm.service.ListGamesService;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	private GameService gameService;

	@Autowired
	private GameDao gameDao;

	@Autowired
	private JoinTokenDao joinTokenDao;

	@Autowired
	private LinkService linkService;

	/**
	 * Gets all active games for the logged in user for viewing via the template games.
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path="/games")
	public String games(Model model, Authentication auth) {
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
			final HttpServletRequest request,
			Model model,
			@ModelAttribute("changeStatus") String changeStatus,
			@ModelAttribute("changeMessage") String changeMessage,
			Principal principal) throws IOException {
		if ( gid == null ) {
			String message = "Could not find that game.";
			response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
			model.addAttribute("message", message);
			// the return to setup will not work - white label error page will take over
			return "error";
		}
		Game game = gameDao.findOne(gid);
		// TODO: this needs a better place and some finesse
		if ( ! game.getOwner().getUsername().equals(principal.getName()) ) {
			boolean found = false;
			for ( Player player : game.getPlayers() ) {
				if ( player.getUser().getUsername().equals(principal.getName()) ) {
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
		model.addAttribute("displayName", game.getGameType().getDisplayName());
		model.addAttribute("game", game);
		model.addAttribute("players", gameService.players(game));
		model.addAttribute("minPlayers", game.getGameType().getMinPlayers());
		// If redirected from a player position change, add model attributes
		if ( StringUtils.isNotEmpty(changeStatus) ) {
			model.addAttribute("changeStatus", changeStatus);
			model.addAttribute("changeMessage", changeMessage);
		}
		else {
			model.addAttribute("changeStatus", false);
		}

		boolean isOwner = false;
		boolean canStart = false;
		boolean hasJoined = false;
		// Allow owner to remove players?
		if ( game.getOwner().getUsername().equals(principal.getName()) ) {
			isOwner = true;
			// Start the game if minimum players have joined
			if ( game.getGameType().getMinPlayers() <= game.getPlayers().size() ) {
				canStart = true;
			}
		}
		for ( Player player : game.getPlayers() ) {
			if ( player.getUser().getUsername().equals(principal.getName()) ) {
				hasJoined = true;
			}
		}
		model.addAttribute("isOwner", isOwner);
		model.addAttribute("canStart", canStart);
		model.addAttribute("hasJoined", hasJoined);
		
		// Unit tests with mocking for this method
		if ( "SETUP".equals(game.getStatus()) ) {
			// add the join token to the game
			if ( game.getGameType().getMaxPlayers() > game.getPlayers().size() ) {
				String token = joinTokenDao.findOneByGame(game).getToken();
				model.addAttribute("token", token);
				String baseUrl = linkService.baseUrl(request.getLocalName(), request.getLocalPort());
				String url = baseUrl + "join?token=" + token;
				model.addAttribute("joinUrl", url);
			}
			
			// TODO: Display/Change player order
			
			return "setup";
		}
		if ( "READY".equals(game.getStatus()) ) {
			// TODO: Allow owner to remove players?
			// TODO: Display/Change player order
			// TODO: Start the game
			return "ready";
		}
		if ( "ACTIVE".equals(game.getStatus()) ) {
			// TODO: make a move
			// TODO: check the board
			// TODO: See past moves
			// TODO: Resign
			return "active";
		}
		// safe to return finished as fall-back as no action available in that template
		// TODO: Add winner? or game status if available
		// TODO: Display final board
		// TODO: Display moves
		return "finished";
	}

	@RequestMapping(method = RequestMethod.GET, path="/allgames/{pageNumber}")
	public String allGames(@PathVariable Integer pageNumber, Model model, Authentication auth) {
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
