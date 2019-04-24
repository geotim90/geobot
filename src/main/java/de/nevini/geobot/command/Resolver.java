package de.nevini.geobot.command;

import java.util.Collection;

public interface Resolver<T> {

    Collection<T> resolve(CommandContext context, String reference);

}
