package de.nevini.geobot.modules.core.game;

import de.nevini.geobot.sharding.ShardEventListener;
import lombok.NonNull;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.RichPresence;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@ShardEventListener
public class GameListener {

    private final Set<Long> idCache = new HashSet<>();

    private final GameHandler gameHandler;

    public GameListener(
            @Autowired @NonNull GameHandler gameHandler
    ) {
        this.gameHandler = gameHandler;
    }

    @SubscribeEvent
    public void onGameUpdate(UserUpdateGameEvent event) {
        updateGameIfRich(event.getOldGame());
        updateGameIfRich(event.getNewGame());
    }

    private void updateGameIfRich(Game game) {
        if (game != null && game.isRich()) {
            final RichPresence richPresence = game.asRichPresence();
            if (game.getType() == Game.GameType.DEFAULT) {
                final long applicationIdLong = richPresence.getApplicationIdLong();
                if (idCache.add(applicationIdLong)) {
                    gameHandler.updateGame(applicationIdLong, richPresence.getName());
                }
            }
        }
    }

}
