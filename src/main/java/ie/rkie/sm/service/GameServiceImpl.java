package ie.rkie.sm.service;

import ie.rkie.sm.db.GameType;
import ie.rkie.sm.db.GameTypeDao;
import ie.rkie.sm.dto.GameSelectionDTO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {
	
	@Autowired
	private GameTypeDao dao;

	@Override
	public List<GameSelectionDTO> gameList() {
		List<GameSelectionDTO> games = new ArrayList<GameSelectionDTO>();
		for ( GameType gameType : dao.findAll() ) {
			GameSelectionDTO dto = new GameSelectionDTO(gameType.getName(),
					gameType.getDisplayName(), gameType.getMinPlayers(),
					gameType.getMaxPlayers());
			games.add(dto);
		}
		return games;
	}

}
