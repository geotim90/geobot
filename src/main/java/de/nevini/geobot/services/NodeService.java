package de.nevini.geobot.services;

import de.nevini.geobot.command.AbstractCommand;
import de.nevini.geobot.command.CommandContext;
import de.nevini.geobot.command.Resolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class NodeService implements Resolver<String> {

    private final ApplicationContext applicationContext;

    public NodeService(@Autowired ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private Collection<String> getNodes() {
        final Set<String> nodes = new HashSet<>();
        final Collection<AbstractCommand> availableCommands = applicationContext.getBeansOfType(AbstractCommand.class).values();
        for (AbstractCommand command : availableCommands) {
            command.getCommandMatchers().forEach(cm -> nodes.add(cm.getNode()));
        }
        return nodes;
    }

    @Override
    public Collection<String> resolve(CommandContext context, String reference) {
        Set<String> result = new LinkedHashSet<>();
        Collection<String> nodes = getNodes();
        for (String ref : reference.split("[^.\\w]+")) {
            nodes.stream().filter(node -> node.startsWith(ref)).forEach(result::add);
        }
        return result;
    }

}
