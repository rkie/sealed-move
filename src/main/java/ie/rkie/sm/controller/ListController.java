package ie.rkie.sm.controller;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.JoinTokenDao;
import ie.rkie.sm.db.User;
import ie.rkie.sm.service.ListGamesService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Demonstration of DAO using crud interface.
 *
 */
// TODO: replace/delete this class.
@Controller
public class ListController {

	@Autowired
	private ListGamesService listGamesService;
	
	@Autowired
	private JoinTokenDao joinTokenDao;
	
	/**
	 * Returns a games this user is involved with.
	 * 
	 * @return
	 */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public String listUsers(Authentication auth) {
		StringBuilder builder = new StringBuilder("<html><body>");
		
		User user = (User) auth.getPrincipal();
		builder.append("<p>Active games you created:<p>");
		List<Game> games = listGamesService.listActiveOwnedGames(user);
		appendGames(builder, games);
		
		builder.append("<p>Active games you have joined:<p>");
		games = listGamesService.listActiveJoinedGames(user);
		appendGames(builder, games);
		
		builder.append("</body></html>");
		return builder.toString();
	}

	public void appendGames(StringBuilder builder, List<Game> games) {
		if ( games == null || games.size() == 0 ) {
			builder.append("No games found.<table>");
		}
		else {
			builder.append("<table>");
		}
		for ( Game game : games ) {
			builder.append("<tr><td>");
			builder.append(game.getGid());
			builder.append("</td><td>");
			builder.append(game.getGameType().getDisplayName());
			builder.append("</td><td>");
			List<String> players = game.getPlayers()
				.stream()
				.map(player -> player.getUser())
				.map(user -> {
					if ( user == game.getOwner()) {
						return "<em>" + user.getUsername() + "</em>";
					}
					else {
						return user.getUsername();
					}
				})
				.collect(Collectors.toList());
			builder.append(players);
			builder.append("</td><td>");
			builder.append(joinTokenDao.findOneByGame(game).getToken());
			builder.append("</td></tr>");
		}
		builder.append("</table>");
	}

}
