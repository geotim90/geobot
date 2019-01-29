package de.nevini.geobot.modules.core.permissions;

import de.nevini.geobot.messaging.AbstractCommand;
import de.nevini.geobot.messaging.CommandMatcher;
import de.nevini.geobot.messaging.MessageContext;
import de.nevini.geobot.modules.Modules;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionsCommand extends AbstractCommand {

    private static final String KEYWORD = "permissions";
    private static final String[] KEYWORDS = {KEYWORD, "permission", "perms", "perm"};

    protected PermissionsCommand() {
        // TODO implement
        super("Use the **permissions** command to configure which users are allowed to execute which commands in which channels.",
                Modules.CORE, KEYWORDS);
    }

    @Override
    public List<CommandMatcher> getCommandMatchers() {
        // TODO implement
        return null;
    }

    @Override
    public void onHelp(MessageContext context) {
        // TODO implement
    }
}
