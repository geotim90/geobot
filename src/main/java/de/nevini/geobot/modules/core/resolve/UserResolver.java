package de.nevini.geobot.modules.core.resolve;

import de.nevini.geobot.messaging.MessageContext;
import de.nevini.geobot.messaging.Resolver;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class UserResolver implements Resolver<Member> {

    @Override
    public Collection<Member> resolve(MessageContext context, String reference) {
        final ArrayList<Member> users = new ArrayList<>();
        context.getMessage().getGuild().getMemberCache().forEach(user -> {
            if (user.getUser().getId().equals(reference) || StringUtils.containsIgnoreCase(user.getEffectiveName(), reference)) {
                users.add(user);
            }
        });
        return users;
    }

}
