package de.nevini.geobot.modules.core.resolve;

import de.nevini.geobot.messaging.MessageContext;
import de.nevini.geobot.messaging.Resolver;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class RoleResolver implements Resolver<Role> {

    @Override
    public Collection<Role> resolve(MessageContext context, String reference) {
        final ArrayList<Role> roles = new ArrayList<>();
        context.getMessage().getGuild().getRoleCache().forEach(role -> {
            if (role.getId().equals(reference) || StringUtils.containsIgnoreCase(role.getName(), reference)) {
                roles.add(role);
            }
        });
        return roles;
    }

}
