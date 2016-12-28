package ie.rkie.sm.service;

import ie.rkie.sm.db.Game;
import ie.rkie.sm.db.GameDao;
import ie.rkie.sm.db.PlayerDao;
import ie.rkie.sm.db.User;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ListGamesServiceImpl implements ListGamesService {
	
	@Value("${sm.games.pagesize}")
	private int pagesize;

	@Autowired
	private PlayerDao playerDao;

	@Autowired
	private GameDao gameDao;

	@Override
	public List<Game> listActiveOwnedGames(User user) {
		List<Game> games = gameDao.findByOwner(user)
				.stream()
				.filter(game -> ! game.getStatus().equals("FINISHED"))
				.collect(Collectors.toList());
		return games;
	}

	@Override
	public List<Game> listActiveJoinedGames(User user) {
		List<Game> games = playerDao.findByUser(user)
				.stream()
				.map(player -> player.getGame())
				.filter(game -> ! game.getStatus().equals("FINISHED"))
				.collect(Collectors.toList());
		return games;
	}

	@Override
	public List<Game> listFinishedOwnedGames(User user) {
		List<Game> games = gameDao.findByOwner(user)
				.stream()
				.filter(game -> game.getStatus().equals("FINISHED"))
				.collect(Collectors.toList());
		return games;
	}

	@Override
	public List<Game> listFinishedJoinedGames(User user) {
		List<Game> games = playerDao.findByUser(user)
				.stream()
				.map(player -> player.getGame())
				.filter(game -> game.getStatus().equals("FINISHED"))
				.collect(Collectors.toList());
		return games;
	}

	@Override
	public Page<Game> listAllGames(User user, int pageNo) {
		PageRequest pageable = new PageRequest(pageNo, pagesize);
		String username = user.getUsername();
		Page<Game> page = gameDao.findByOwnerUsernameOrPlayersUserUsername(pageable, username, username);;
		return page;
	}

}
