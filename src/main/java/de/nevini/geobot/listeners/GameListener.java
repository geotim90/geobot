package de.nevini.geobot.listeners;

import de.nevini.geobot.services.GameService;
import de.nevini.geobot.sharding.ShardEventListener;
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

    private final GameService gameService;

    public GameListener(@Autowired GameService gameService) {
        this.gameService = gameService;
    }

    @SubscribeEvent
    public void onUserUpdateGame(UserUpdateGameEvent e) {
        processGame(e.getOldGame().asRichPresence());
        processGame(e.getNewGame().asRichPresence());
    }

    private void processGame(RichPresence presence) {
        if (presence != null && presence.getType() == Game.GameType.DEFAULT) {
            final long id = presence.getApplicationIdLong();
            if (idCache.add(id)) {
                gameService.saveGame(id, presence.getName());
            }
        }
    }

}
