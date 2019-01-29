package de.nevini.geobot.messaging;

import java.util.Collection;

public interface Resolver<T> {

    Collection<T> resolve(MessageContext context, String reference);

}
