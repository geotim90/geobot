package de.nevini.geobot.command;

import de.nevini.geobot.data.game.Game;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Resolvers {

    private final Resolver<TextChannel> channelResolver;
    private final Resolver<Game> gameResolver;
    private final Resolver<String> nodeResolver;
    private final Resolver<Role> roleResolver;
    private final Resolver<Member> userResolver;

    public Resolvers(
            @Autowired Resolver<TextChannel> channelResolver,
            @Autowired Resolver<Game> gameResolver,
            @Autowired Resolver<String> nodeResolver,
            @Autowired Resolver<Role> roleResolver,
            @Autowired Resolver<Member> userResolver
    ) {
        this.channelResolver = channelResolver;
        this.gameResolver = gameResolver;
        this.nodeResolver = nodeResolver;
        this.roleResolver = roleResolver;
        this.userResolver = userResolver;
    }

}
