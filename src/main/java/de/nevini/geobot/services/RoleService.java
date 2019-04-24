package de.nevini.geobot.services;

import de.nevini.geobot.command.CommandContext;
import de.nevini.geobot.command.Resolver;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class RoleService implements Resolver<Role> {

    @Override
    public Collection<Role> resolve(CommandContext context, String reference) {
        return context.getMessage().getGuild().getRoleCache().stream()
                .filter(role -> role.getId().equals(reference) || StringUtils.containsIgnoreCase(role.getName(), reference))
                .collect(Collectors.toList());
    }

}
