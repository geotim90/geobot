package de.nevini.geobot.services;

import de.nevini.geobot.command.CommandContext;
import de.nevini.geobot.command.Resolver;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ChannelService implements Resolver<TextChannel> {

    @Override
    public Collection<TextChannel> resolve(CommandContext context, String reference) {
        return context.getMessage().getGuild().getTextChannelCache().stream()
                .filter(channel -> channel.getId().equals(reference) || StringUtils.containsIgnoreCase(channel.getName(), reference))
                .collect(Collectors.toList());
    }

}
