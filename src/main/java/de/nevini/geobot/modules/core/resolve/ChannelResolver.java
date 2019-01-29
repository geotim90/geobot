package de.nevini.geobot.modules.core.resolve;

import de.nevini.geobot.messaging.MessageContext;
import de.nevini.geobot.messaging.Resolver;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ChannelResolver implements Resolver<TextChannel> {

    @Override
    public Collection<TextChannel> resolve(MessageContext context, String reference) {
        final ArrayList<TextChannel> channels = new ArrayList<>();
        context.getMessage().getGuild().getTextChannelCache().forEach(channel -> {
            if (channel.getId().equals(reference) || StringUtils.containsIgnoreCase(channel.getName(), reference)) {
                channels.add(channel);
            }
        });
        return channels;
    }

}
