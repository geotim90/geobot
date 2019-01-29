package de.nevini.geobot.modules.core.resolve;

import de.nevini.geobot.data.game.Game;
import de.nevini.geobot.messaging.MessageContext;
import de.nevini.geobot.messaging.Resolver;
import de.nevini.geobot.modules.core.game.GameHandler;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GameResolver implements Resolver<Game> {

    private final GameHandler gameHandler;

    public GameResolver(
            @Autowired @NonNull GameHandler gameHandler
    ) {
        this.gameHandler = gameHandler;
    }

    @Override
    public Collection<Game> resolve(MessageContext context, String reference) {
        return gameHandler.find(reference);
    }

}
