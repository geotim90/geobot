package de.nevini.geobot.messaging;

import de.nevini.geobot.data.game.Game;
import de.nevini.geobot.modules.core.modules.ModulesHandler;
import de.nevini.geobot.modules.core.permissions.PermissionsHandler;
import de.nevini.geobot.modules.core.prefix.PrefixHandler;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MessageHandlers {

    private final ModulesHandler modulesHandler;
    private final PermissionsHandler permissionsHandler;
    private final PrefixHandler prefixHandler;

    private final Resolver<TextChannel> channelResolver;
    private final Resolver<Game> gameResolver;
    private final Resolver<String> nodeResolver;
    private final Resolver<Role> roleResolver;
    private final Resolver<Member> userResolver;

    public MessageHandlers(
            @Autowired @NonNull ModulesHandler modulesHandler,
            @Autowired @NonNull PermissionsHandler permissionsHandler,
            @Autowired @NonNull PrefixHandler prefixHandler,

            @Autowired @NonNull Resolver<TextChannel> channelResolver,
            @Autowired @NonNull Resolver<Game> gameResolver,
            @Autowired @NonNull Resolver<String> nodeResolver,
            @Autowired @NonNull Resolver<Role> roleResolver,
            @Autowired @NonNull Resolver<Member> userResolver
    ) {
        this.modulesHandler = modulesHandler;
        this.permissionsHandler = permissionsHandler;
        this.prefixHandler = prefixHandler;

        this.channelResolver = channelResolver;
        this.gameResolver = gameResolver;
        this.nodeResolver = nodeResolver;
        this.roleResolver = roleResolver;
        this.userResolver = userResolver;
    }

}
