package de.nevini.geobot.modules.core;

import de.nevini.geobot.command.AbstractCommand;
import de.nevini.geobot.command.CommandContext;
import de.nevini.geobot.command.CommandMatcher;
import de.nevini.geobot.modules.Modules;
import de.nevini.geobot.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PermissionsCommand extends AbstractCommand {

    private static final String KEYWORD = "permissions";
    private static final String[] KEYWORDS = {KEYWORD, "permission", "perms", "perm"};

    private final CommandMatcher getMatcher = new CommandMatcher(
            Pattern.compile("(?i)(get|display|list|show)?"),
            getNode() + ".get",
            (context, matcher) -> onGet(context),
            (context, matcher) -> onHelpGet(context)
    );

    private final CommandMatcher debugMatcher = new CommandMatcher(
            Pattern.compile("(?i)debug"),
            getNode() + ".debug",
            (context, matcher) -> onDebug(context),
            (context, matcher) -> onHelpDebug(context)
    );

    private final CommandMatcher allowMatcher = new CommandMatcher(
            Pattern.compile("(?i)(allow|add|grant)?"),
            getNode() + ".allow",
            (context, matcher) -> onAllow(context),
            (context, matcher) -> onHelpAllow(context)
    );

    private final CommandMatcher denyMatcher = new CommandMatcher(
            Pattern.compile("(?i)(deny|block|refuse)?"),
            getNode() + ".deny",
            (context, matcher) -> onDeny(context),
            (context, matcher) -> onHelpDeny(context)
    );

    private final CommandMatcher resetMatcher = new CommandMatcher(
            Pattern.compile("(?i)(reset|clear|default|remove)?"),
            getNode() + ".reset",
            (context, matcher) -> onReset(context),
            (context, matcher) -> onHelpReset(context)
    );

    private final PermissionService permissionService;

    public PermissionsCommand(@Autowired PermissionService permissionService) {
        super("Use the **permissions** command to configure which users are allowed to execute which commands in which channels.",
                Modules.CORE, KEYWORDS);
        this.permissionService = permissionService;
    }

    @Override
    public List<CommandMatcher> getCommandMatchers() {
        return Arrays.asList(
                getMatcher,
                debugMatcher,
                allowMatcher,
                denyMatcher,
                resetMatcher
        );
    }

    private void onGet(CommandContext context) {
        // TODO implement
    }

    private void onDebug(CommandContext context) {
        // TODO implement
    }

    private void onAllow(CommandContext context) {
        // TODO implement
    }

    private void onDeny(CommandContext context) {
        // TODO implement
    }

    private void onReset(CommandContext context) {
        // TODO implement
    }

    @Override
    public void onHelp(CommandContext context) {
        context.getResponder().reply("use the **permissions** command to configure which users are allowed to execute which commands in which channels.\n" +
                "\n" +
                "Server administrators are not restricted by permissions.\n" +
                "Permissions are denied to everyone else by default.\n" +
                "\n" +
                "Users cannot configure permissions for roles above their own or grant permissions for nodes they do not have access to themselves.\n" +
                "Users cannot configure permissions for users with roles above their own or grant permissions for nodes they do not have access to themselves.\n" +
                "\n" +
                "__**Commands**__\n" +
                "**permissions** (get) \\<*target*\\> - Lists the effective permissions for the provided *target* (see options below). \n" +
                "**permissions** debug \\<*target*\\> - Lists all relevant permission node configurations for the provided *target* (see options below).\n" +
                "**permissions** allow \\<*nodes*\\> \\<*target*\\> - Grants the provided permission *nodes* to the provided *target* (see options below).\n" +
                "**permissions** deny \\<*nodes*\\> \\<*target*\\> - Refuses the provided permission *nodes* to the provided *target* (see options below).\n" +
                "**permissions** reset \\<*nodes*\\> \\<*target*\\> - Resets the provided permission *nodes* for the provided *target* (see options below).\n" +
                "\n" +
                "__**Options**__\n" +
                "--all - Short-hand for all permission nodes.\n" +
                "--channel \\[*channel*\\] - Used to get or set *channel* permissions.\n" +
                "--node \\<*nodes*\\> - Permission *nodes* may be provided to check specific permissions.\n" +
                "--server - Used to get or set server permissions.\n" +
                "--role \\<*role*\\> - Used to get or set *role* permissions.\n" +
                "--user \\[*user*\\] - Used to get or set *user* permissions.\n" +
                "\n" +
                "__**Alternative syntax**__\n" +
                "You can also use `add` or `grant` instead of `allow`.\n" +
                "You can also use `block` or `refuse` instead of `deny`.\n" +
                "You can also use `clear`, `default` or `remove` instead of `reset`.");
    }

    private void onHelpGet(CommandContext context) {
        context.getResponder().reply("__**Command**__\n" +
                "**permissions** (get) \\<*target*\\> - Lists the effective permissions for the provided *target* (see options below). \n" +
                "\n" +
                "__**Options**__\n" +
                "--all - Short-hand for all permission nodes.\n" +
                "--channel \\[*channel*\\] - Used to get or set *channel* permissions.\n" +
                "--node \\<*nodes*\\> - Permission *nodes* may be provided to check specific permissions.\n" +
                "--server - Used to get or set server permissions.\n" +
                "--role \\<*role*\\> - Used to get or set *role* permissions.\n" +
                "--user \\[*user*\\] - Used to get or set *user* permissions.");
    }

    private void onHelpDebug(CommandContext context) {
        context.getResponder().reply("__**Command**__\n" +
                "**permissions** debug \\<*target*\\> - Lists all relevant permission nodes configurations for the provided *target* (see options below).\n" +
                "\n" +
                "__**Options**__\n" +
                "--all - Short-hand for all permission nodes.\n" +
                "--channel \\[*channel*\\] - Used to get or set *channel* permissions.\n" +
                "--node \\<*nodes*\\> - Permission *nodes* may be provided to check specific permissions.\n" +
                "--server - Used to get or set server permissions.\n" +
                "--role \\<*role*\\> - Used to get or set *role* permissions.\n" +
                "--user \\[*user*\\] - Used to get or set *user* permissions.");
    }

    private void onHelpAllow(CommandContext context) {
        context.getResponder().reply("__**Command**__\n" +
                "**permissions** allow \\<*nodes*\\> \\<*target*\\> - Grants the provided permission *nodes* to the provided *target* (see options below).\n" +
                "\n" +
                "__**Options**__\n" +
                "--all - Short-hand for all permission nodes.\n" +
                "--channel \\[*channel*\\] - Used to get or set *channel* permissions.\n" +
                "--node \\<*nodes*\\> - Permission *nodes* may be provided to check specific permissions.\n" +
                "--server - Used to get or set server permissions.\n" +
                "--role \\<*role*\\> - Used to get or set *role* permissions.\n" +
                "--user \\[*user*\\] - Used to get or set *user* permissions.\n" +
                "\n" +
                "__**Alternative syntax**__\n" +
                "You can also use `add` or `grant` instead of `allow`.");
    }

    private void onHelpDeny(CommandContext context) {
        context.getResponder().reply("__**Command**__\n" +
                "**permissions** deny \\<*nodes*\\> \\<*target*\\> - Refuses the provided permission *nodes* to the provided *target* (see options below).\n" +
                "\n" +
                "__**Options**__\n" +
                "--all - Short-hand for all permission nodes.\n" +
                "--channel \\[*channel*\\] - Used to get or set *channel* permissions.\n" +
                "--node \\<*nodes*\\> - Permission *nodes* may be provided to check specific permissions.\n" +
                "--server - Used to get or set server permissions.\n" +
                "--role \\<*role*\\> - Used to get or set *role* permissions.\n" +
                "--user \\[*user*\\] - Used to get or set *user* permissions.\n" +
                "\n" +
                "__**Alternative syntax**__\n" +
                "You can also use `block` or `refuse` instead of `deny`.");
    }

    private void onHelpReset(CommandContext context) {
        context.getResponder().reply("__**Command**__\n" +
                "**permissions** reset \\<*nodes*\\> \\<*target*\\> - Resets the provided permission *nodes* for the provided *target* (see options below).\n" +
                "\n" +
                "__**Options**__\n" +
                "--all - Short-hand for all permission nodes.\n" +
                "--channel \\[*channel*\\] - Used to get or set *channel* permissions.\n" +
                "--node \\<*nodes*\\> - Permission *nodes* may be provided to check specific permissions.\n" +
                "--server - Used to get or set server permissions.\n" +
                "--role \\<*role*\\> - Used to get or set *role* permissions.\n" +
                "--user \\[*user*\\] - Used to get or set *user* permissions.\n" +
                "\n" +
                "__**Alternative syntax**__\n" +
                "You can also use `clear`, `default` or `remove` instead of `reset`.");
    }

}
