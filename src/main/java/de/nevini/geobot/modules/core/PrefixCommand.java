package de.nevini.geobot.modules.core;

import de.nevini.geobot.command.AbstractCommand;
import de.nevini.geobot.command.CommandContext;
import de.nevini.geobot.command.CommandMatcher;
import de.nevini.geobot.modules.Modules;
import de.nevini.geobot.services.PrefixService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PrefixCommand extends AbstractCommand {

    private static final String KEYWORD = "prefix";
    private static final int MAX_LENGTH = 24;

    private final CommandMatcher getMatcher = new CommandMatcher(
            Pattern.compile("(?i)(?:get|display|list|show)?"),
            getNode() + ".get",
            (context, matcher) -> onGet(context),
            (context, matcher) -> onHelpGet(context)
    );

    private final CommandMatcher setMatcher = new CommandMatcher(
            Pattern.compile("(?i)set\\s+(.+)"),
            getNode() + ".set",
            this::onSet,
            (context, matcher) -> onHelpSet(context)
    );

    private final PrefixService prefixService;

    public PrefixCommand(@Autowired PrefixService prefixService) {
        super("Use the **prefix** command to change the Geobot prefix on your server.",
                Modules.CORE, KEYWORD);
        this.prefixService = prefixService;
    }

    @Override
    public List<CommandMatcher> getCommandMatchers() {
        return Arrays.asList(
                getMatcher,
                setMatcher
        );
    }

    private void onGet(CommandContext context) {
        final String prefix = prefixService.getServerPrefix(context.getGuild());
        context.getResponder().reply("the prefix is currently set to `" + prefix + "`.");
    }

    private void onSet(CommandContext context, Matcher matcher) {
        final String prefix = StringUtils.defaultString(matcher.group(1), "");
        if (StringUtils.isEmpty(prefix)) {
            context.getResponder().replyError("please provide a prefix to set.");
        } else if (prefix.length() > MAX_LENGTH) {
            context.getResponder().replyError("the prefix must not be longer than " + MAX_LENGTH + " characters.");
        } else {
            prefixService.setServerPrefix(context.getMessage().getGuild(), prefix);
            context.getResponder().replyOk("the prefix has been set to `" + prefix + "`.");
        }
    }

    @Override
    public void onHelp(CommandContext context) {
        context.getResponder().reply("use the **prefix** command to change the Geobot prefix on your server.\n" +
                "\n" +
                "The default prefix is `G>`.\n" +
                "Regardless of the currently set prefix, commands can always be executed by mentioning the bot directly (e.g. `@Geobot`).\n" +
                "\n" +
                "__**Commands**__\n" +
                "**prefix** (get) - Displays the currently set prefix.\n" +
                "**prefix** set \\<*prefix*\\> - Changes the prefix to the specified *prefix*.");
    }

    private void onHelpGet(CommandContext context) {
        if (context.getArguments().getArgument().orElse("").isEmpty()) {
            onHelp(context);
        } else {
            context.getResponder().reply("__**Command**__\n" +
                    "**prefix** (get) - Displays the currently set prefix.");
        }
    }

    private void onHelpSet(CommandContext context) {
        context.getResponder().reply("__**Command**__\n" +
                "**prefix** set \\<*prefix*\\> - Changes the prefix to the specified *prefix*.");
    }

}
