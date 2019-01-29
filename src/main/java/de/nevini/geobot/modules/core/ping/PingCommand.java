package de.nevini.geobot.modules.core.ping;

import de.nevini.geobot.messaging.AbstractCommand;
import de.nevini.geobot.messaging.CommandMatcher;
import de.nevini.geobot.messaging.MessageContext;
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

    protected PingCommand() {
        super("Use the **ping** command to measure the latency between Discord and Geobot.",
                Modules.CORE, KEYWORD);
    }

    @Override
    public List<CommandMatcher> getCommandMatchers() {
        return Collections.singletonList(
                pingMatcher
        );
    }

    private void onPing(MessageContext context) {
        long latency = context.getMessage().getJDA().getPing();
        context.reply("I measured a " + latency + "ms latency between Geobot and Discord.");
    }

    @Override
    public void onHelp(MessageContext context) {
        context.reply("use the **ping** command to measure the latency between Discord and Geobot.\n" +
                "\n" +
                "__**Command**__\n" +
                "**ping** - Displays the latency between Discord and Geobot.");
    }
}
