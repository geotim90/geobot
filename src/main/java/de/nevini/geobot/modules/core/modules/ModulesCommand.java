package de.nevini.geobot.modules.core.modules;

import de.nevini.geobot.messaging.*;
import de.nevini.geobot.modules.Modules;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ModulesCommand extends AbstractCommand {

    private final static String KEYWORD = "modules";

    private final CommandMatcher getMatcher = new CommandMatcher(
            Pattern.compile(Patterns.GET + "?"),
            getNode() + ".get",
            (context, matcher) -> onGet(context),
            (context, matcher) -> onHelpGet(context)
    );

    private final CommandMatcher addMatcher = new CommandMatcher(
            Pattern.compile("(?i)(?:add|activate|enable|install)(?:\\s+(.+))?"),
            getNode() + ".add",
            this::onAdd,
            (context, matcher) -> onHelpAdd(context)
    );

    private final CommandMatcher removeMatcher = new CommandMatcher(
            Pattern.compile("(?i)(?:remove|deactivate|disable|uninstall)(?:\\s+(.+))?"),
            getNode() + ".remove",
            this::onRemove,
            (context, matcher) -> onHelpRemove(context)
    );

    private final ModulesHandler modulesHandler;

    protected ModulesCommand(
            @Autowired @NonNull ModulesHandler modulesHandler
    ) {
        super("Use the **modules** command to activate or deactivate Geobot modules on your server.",
                Modules.CORE, KEYWORD);
        this.modulesHandler = modulesHandler;
    }

    @Override
    public List<CommandMatcher> getCommandMatchers() {
        return Arrays.asList(
                getMatcher,
                addMatcher,
                removeMatcher
        );
    }

    private void onGet(MessageContext context) {
        final Map<String, Boolean> modules = modulesHandler.list(context.getMessage().getGuild());
        final StringBuilder sb = new StringBuilder("here is the list of Geobot modules and whether they are active or not.\n");
        modules.forEach((k, v) -> sb.append("\n").append(v ? Icons.MODULE_ACTIVE : Icons.MODULE_INACTIVE).append(" ").append(k));
        context.reply(sb.toString());
    }

    private void onAdd(MessageContext context, Matcher matcher) {
        final String module = StringUtils.defaultString(matcher.group(1), StringUtils.EMPTY).toLowerCase();
        if (!Modules.list().contains(module)) {
            context.replyError("please provide a valid module name to activate.");
        } else if (Modules.CORE.equals(module)) {
            context.replyOk("there is no need to activate the core module - it is always active.");
        } else if (modulesHandler.isActive(context.getMessage().getGuild(), module)) {
            context.replyOk("there is no need to activate the " + module + " module - it is already active.");
        } else {
            modulesHandler.activate(context.getMessage().getGuild(), module);
            context.replyOk("the " + module + " is now active.");
        }
    }

    private void onRemove(MessageContext context, Matcher matcher) {
        final String module = StringUtils.defaultString(matcher.group(1), StringUtils.EMPTY).toLowerCase();
        if (!Modules.list().contains(module)) {
            context.replyError("please provide a valid module name to disable.");
        } else if (Modules.CORE.equals(module)) {
            context.replyError("you cannot disable the core module - it is always active.");
        } else if (!modulesHandler.isActive(context.getMessage().getGuild(), module)) {
            context.replyOk("there is no need to disable the " + module + " module - it is already inactive.");
        } else {
            modulesHandler.disable(context.getMessage().getGuild(), module);
            context.replyOk("the " + module + " is now inactive.");
        }
    }

    @Override
    public void onHelp(MessageContext context) {
        context.reply("use the **modules** command to activate or deactivate Geobot modules on your server.\n" +
                "\n" +
                "__**Commands**__\n" +
                "**modules** (get) - Lists all modules and whether they are active for the current server.\n" +
                "**modules** add \\<*module*\\> - Activates a *module* for the current server.\n" +
                "**modules** remove \\<*module*\\> - Disables a *module* for the current server.\n" +
                "\n" +
                "__**Alternative syntax**__\n" +
                "You can also use `activate`, `enable` or `install` instead of `add`.\n" +
                "You can also use `deactivate`, `disable` or `uninstall` instead of `remove`.");
    }

    private void onHelpGet(MessageContext context) {
        context.reply("__**Command**__\n" +
                "**modules** (get) - Lists all modules and whether they are active for the current server.");
    }

    private void onHelpAdd(MessageContext context) {
        context.reply("__**Command**__\n" +
                "**modules** add \\<*module*\\> - Activates a *module* for the current server.\n" +
                "\n" +
                "__**Alternative syntax**__\n" +
                "You can also use `activate`, `enable` or `install` instead of `add`.");
    }

    private void onHelpRemove(MessageContext context) {
        context.reply("__**Command**__\n" +
                "**modules** remove \\<*module*\\> - Disables a *module* for the current server.\n" +
                "\n" +
                "__**Alternative syntax**__\n" +
                "You can also use `deactivate`, `disable` or `uninstall` instead of `remove`.");
    }

}
