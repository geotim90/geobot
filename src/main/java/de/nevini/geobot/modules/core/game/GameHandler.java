package de.nevini.geobot.modules.core.game;

import de.nevini.geobot.data.game.Game;
import de.nevini.geobot.data.game.GameRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class GameHandler {

    private final GameRepository gameRepository;

    public GameHandler(
            @Autowired @NonNull GameRepository gameRepository
    ) {
        this.gameRepository = gameRepository;
    }

    public Collection<Game> find(String lookup) {
        if (lookup.matches("\\d+")) {
            return gameRepository.findAllByIdOrNameContainingIgnoreCase(Long.parseUnsignedLong(lookup), lookup);
        } else {
            return gameRepository.findAllByNameContainingIgnoreCase(lookup);
        }
    }

    public void updateGame(long id, String name) {
        final Game bean = new Game(id, name);
        log.info("Storing bean: " + bean.toString());
        gameRepository.save(bean);
    }

}
