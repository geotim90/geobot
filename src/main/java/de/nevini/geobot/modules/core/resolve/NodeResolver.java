package de.nevini.geobot.modules.core.resolve;

import de.nevini.geobot.messaging.MessageContext;
import de.nevini.geobot.messaging.Resolver;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class NodeResolver implements Resolver<String> {

    @Override
    public Collection<String> resolve(MessageContext context, String reference) {
        // TODO implement
        return null;
    }

}
