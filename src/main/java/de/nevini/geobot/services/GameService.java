package de.nevini.geobot.services;

import de.nevini.geobot.command.CommandContext;
import de.nevini.geobot.command.Resolver;
import de.nevini.geobot.data.game.Game;
import de.nevini.geobot.data.game.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GameService implements Resolver<Game> {

    private final GameRepository gameRepository;

    public GameService(@Autowired GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Collection<Game> resolve(CommandContext context, String reference) {
        try {
            final long id = Long.parseLong(reference);
            return gameRepository.findAllByIdOrNameContainsIgnoreCase(id, reference);
        } catch (NumberFormatException e) {
            return gameRepository.findAllByNameContainsIgnoreCase(reference);
        }
    }

    public void saveGame(long id, String name) {
        gameRepository.save(new Game(id, name));
    }

}
