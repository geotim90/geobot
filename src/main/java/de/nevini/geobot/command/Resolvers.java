package de.nevini.geobot.command;

import lombok.Getter;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Resolvers {

    private final Resolver<TextChannel> channelResolver;

    public Resolvers(
            @Autowired Resolver<TextChannel> channelResolver
    ) {
        this.channelResolver = channelResolver;
    }

}
