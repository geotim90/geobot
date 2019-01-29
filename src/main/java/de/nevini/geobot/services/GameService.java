package de.nevini.geobot.services;

import de.nevini.geobot.data.game.Game;
import de.nevini.geobot.data.game.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(@Autowired GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void saveGame(long id, String name) {
        gameRepository.save(new Game(id, name));
    }

}
