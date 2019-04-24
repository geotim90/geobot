package de.nevini.geobot.modules.core;

import de.nevini.geobot.command.AbstractCommand;
import de.nevini.geobot.command.CommandContext;
import de.nevini.geobot.command.CommandMatcher;
import de.nevini.geobot.modules.Modules;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PingCommand extends AbstractCommand {

    private static final String KEYWORD = "ping";

    private final CommandMatcher pingMatcher = new CommandMatcher(
            Pattern.compile(".*"),
            getNode(),
            (context, matcher) -> onPing(context),
            (context, matcher) -> onHelp(context)
    );

    public PingCommand() {
        super("Use the **ping** command to measure the latency between Discord and Geobot.",
                Modules.CORE, KEYWORD);
    }

    @Override
    public List<CommandMatcher> getCommandMatchers() {
        return Collections.singletonList(
                pingMatcher
        );
    }

    private void onPing(CommandContext context) {
        long latency = context.getMessage().getJDA().getPing();
        context.getResponder().reply("I measured a " + latency + "ms latency between Geobot and Discord.");
    }

    @Override
    public void onHelp(CommandContext context) {
        context.getResponder().reply("use the **ping** command to measure the latency between Discord and Geobot.\n" +
                "\n" +
                "__**Command**__\n" +
                "**ping** - Displays the latency between Discord and Geobot.");
    }

}
