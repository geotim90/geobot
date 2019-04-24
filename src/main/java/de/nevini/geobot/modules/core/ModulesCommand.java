package de.nevini.geobot.modules.core;

import de.nevini.geobot.command.AbstractCommand;
import de.nevini.geobot.command.CommandContext;
import de.nevini.geobot.command.CommandMatcher;
import de.nevini.geobot.modules.Modules;
import de.nevini.geobot.services.ModuleService;
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

    private static final String EMOTE_ACTIVE = "✅";
    private static final String EMOTE_INACTIVE = "\uD83D\uDEAB";

    private final CommandMatcher getMatcher = new CommandMatcher(
            Pattern.compile("(?i)(?:get|display|list|show)?"),
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

    private final ModuleService moduleService;

    protected ModulesCommand(@Autowired ModuleService moduleService) {
        super("Use the **modules** command to activate or deactivate Geobot modules on your server.",
                Modules.CORE, KEYWORD);
        this.moduleService = moduleService;
    }

    @Override
    public List<CommandMatcher> getCommandMatchers() {
        return Arrays.asList(
                getMatcher,
                addMatcher,
                removeMatcher
        );
    }

    private void onGet(CommandContext context) {
        final Map<String, Boolean> modules = moduleService.getAllModuleActivity(context.getMessage().getGuild());
        final StringBuilder sb = new StringBuilder("here is the list of Geobot modules and whether they are active or not.\n");
        modules.forEach((k, v) -> sb.append("\n").append(v ? EMOTE_ACTIVE : EMOTE_INACTIVE).append(" ").append(k));
        context.getResponder().reply(sb.toString());
    }

    private void onAdd(CommandContext context, Matcher matcher) {
        final String module = StringUtils.defaultString(matcher.group(1), StringUtils.EMPTY).toLowerCase();
        if (!Modules.list().contains(module)) {
            context.getResponder().replyError("please provide a valid module name to activate.");
        } else if (Modules.CORE.equals(module)) {
            context.getResponder().replyOk("there is no need to activate the core module - it is always active.");
        } else if (moduleService.isModuleActive(context.getMessage().getGuild(), module)) {
            context.getResponder().replyOk("there is no need to activate the " + module + " module - it is already active.");
        } else {
            moduleService.setModuleActive(context.getMessage().getGuild(), module, true);
            context.getResponder().replyOk("the " + module + " is now active.");
        }
    }

    private void onRemove(CommandContext context, Matcher matcher) {
        final String module = StringUtils.defaultString(matcher.group(1), StringUtils.EMPTY).toLowerCase();
        if (!Modules.list().contains(module)) {
            context.getResponder().replyError("please provide a valid module name to disable.");
        } else if (Modules.CORE.equals(module)) {
            context.getResponder().replyError("you cannot disable the core module - it is always active.");
        } else if (!moduleService.isModuleActive(context.getMessage().getGuild(), module)) {
            context.getResponder().replyOk("there is no need to disable the " + module + " module - it is already inactive.");
        } else {
            moduleService.setModuleActive(context.getMessage().getGuild(), module, false);
            context.getResponder().replyOk("the " + module + " is now inactive.");
        }
    }

    @Override
    public void onHelp(CommandContext context) {
        context.getResponder().reply("use the **modules** command to activate or deactivate Geobot modules on your server.\n" +
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

    private void onHelpGet(CommandContext context) {
        context.getResponder().reply("__**Command**__\n" +
                "**modules** (get) - Lists all modules and whether they are active for the current server.");
    }

    private void onHelpAdd(CommandContext context) {
        context.getResponder().reply("__**Command**__\n" +
                "**modules** add \\<*module*\\> - Activates a *module* for the current server.\n" +
                "\n" +
                "__**Alternative syntax**__\n" +
                "You can also use `activate`, `enable` or `install` instead of `add`.");
    }

    private void onHelpRemove(CommandContext context) {
        context.getResponder().reply("__**Command**__\n" +
                "**modules** remove \\<*module*\\> - Disables a *module* for the current server.\n" +
                "\n" +
                "__**Alternative syntax**__\n" +
                "You can also use `deactivate`, `disable` or `uninstall` instead of `remove`.");
    }

}
