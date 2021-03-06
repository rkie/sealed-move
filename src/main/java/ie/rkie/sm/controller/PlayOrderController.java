package ie.rkie.sm.controller;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.Player;
import ie.rkie.sm.db.PlayerDao;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/changeorder")
public class PlayOrderController {
	
	@Autowired
	private GameDao gameDao;
	
	@Autowired
	private PlayerDao playerDao;

	@RequestMapping(method = RequestMethod.GET)
	public String changeOrder(
			final HttpServletResponse response,
			@RequestParam(name="gameid") Integer gameId,
			@RequestParam(name="playOrder") Integer playOrder,
			@RequestParam(name="direction") String direction,
			Model model,
			Principal principal,
			RedirectAttributes redirectAttributes) throws IOException {
		
		// Check game exists
		Game game = gameDao.findOne(gameId);
		if ( game == null ) {
			String message = "Could not find that game.";
			response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
			model.addAttribute("message", message);
			return "error";
		}
		
		// Check authorised to make a change
		if ( ! principal.getName().equals(game.getOwner().getUsername()) 
				|| "FINISHED".equals(game.getStatus()) 
				|| "ACTIVE".equals(game.getStatus()) ) {
			String message = "You cannot perform that action.";
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
			model.addAttribute("message", message);
			return "error";
		}
		
		// Check direction and validity of change
		if ( ! "up".equals(direction) && ! "down".equals(direction) ) {
			String message = "An invalid action was requested: " + direction;
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
			model.addAttribute("message", message);
			return "error";
		}
		List<Player> players = game.getPlayers();
		final int numPlayers = players.size();
		String redirectToGame = "redirect:/game?gameid=" + gameId;
		int newPosition = (direction.equals("up") ? playOrder + 1 : playOrder - 1);
		if ( newPosition < 1 || newPosition > numPlayers ) {
			String message = "Request to move a player outide the possible range";
			redirectAttributes.addFlashAttribute("changeStatus", "warning");
			redirectAttributes.addFlashAttribute("changeMessage", message);
			return redirectToGame;
		}
		
		// change play order of chosen player
		Player player = playerDao.findOneByGameAndPlayOrder(game, playOrder);
		Player affectedPlayer = playerDao.findOneByGameAndPlayOrder(game, newPosition);
		player.setPlayOrder(newPosition);
		
		// change play order of affected player
		affectedPlayer.setPlayOrder(playOrder);
		playerDao.save(player);
		playerDao.save(affectedPlayer);
		
		// redirect back to the game web page with a message to the indicate success
		redirectAttributes.addFlashAttribute("changeStatus", "success");
		redirectAttributes.addFlashAttribute("changeMessage", "Position successfully changed");
		return redirectToGame;
	}

}
