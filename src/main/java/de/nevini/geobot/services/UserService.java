package de.nevini.geobot.services;

import de.nevini.geobot.command.CommandContext;
import de.nevini.geobot.command.Resolver;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService implements Resolver<Member> {

    @Override
    public Collection<Member> resolve(CommandContext context, String reference) {
        return context.getMessage().getGuild().getMemberCache().stream()
                .filter(user -> user.getUser().getId().equals(reference) || StringUtils.containsIgnoreCase(user.getEffectiveName(), reference))
                .collect(Collectors.toList());
    }

}
