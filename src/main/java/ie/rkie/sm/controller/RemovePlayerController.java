package ie.rkie.sm.controller;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.Player;
import ie.rkie.sm.db.PlayerDao;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/removeplayer")
public class RemovePlayerController {
	
	@Autowired
	private GameDao gameDao;
	
	@Autowired
	private PlayerDao playerDao;
	
	@RequestMapping(method = RequestMethod.GET)
	public String deletePlayer(
			final HttpServletResponse response,
			@RequestParam(name="gameid") Integer gameId,
			@RequestParam(name="playOrder") Integer playOrder,
			Model model,
			Principal principal,
			RedirectAttributes redirectAttributes) throws IOException {

		// Check the game exists
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
		final String redirect = "redirect:/game?gameid=" + gameId;
		
		// Find the player
		Player player = playerDao.findOneByGameAndPlayOrder(game, playOrder);
		
		// Return issue if not found
		if ( player == null ) {
			String message = "Attempt to remove a player has failed";
			redirectAttributes.addFlashAttribute("changeStatus", "warning");
			redirectAttributes.addFlashAttribute("changeMessage", message);
			return redirect;
		}
		
		// Remove the player
		final int oldOrder = player.getPlayOrder();
		game.getPlayers().remove(player);
		playerDao.delete(player);
		
		// Reduce play order of remaining?? or do this in service
		for ( Player p : game.getPlayers() ) {
			if ( p.getPlayOrder() > oldOrder ) {
				p.setPlayOrder(p.getPlayOrder() - 1);
			}
		}
		
		// Indicate success with a flash attribute
		redirectAttributes.addFlashAttribute("changeStatus", "success");
		redirectAttributes.addFlashAttribute("changeMessage", "Player has been removed");
		
		// Redirect to the game page
		return redirect;
	}

}
