package de.nevini.geobot.command;

import de.nevini.geobot.services.PrefixService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.data.util.Lazy;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Value
public class CommandContext {

    private static final Pattern COMMAND_PATTERN = Pattern.compile("(?i)(?:help\\s+)?(\\w+)(?:\\s+.+)?");
    private static final Pattern HELP_COMMAND_PATTERN = Pattern.compile("(?i)(?:help)(?:\\s+.+)?");
    private static final Pattern HELP_FLAG_PATTERN = Pattern.compile("(?i)(?:(?:--|//)help|[-/]\\?)");

    private final GuildMessageReceivedEvent event;

    private final Lazy<Boolean> relevantCommand = Lazy.of(this::_isRelevantCommand);
    private final Lazy<String> prefix = Lazy.of(this::_getPrefix);
    private final Lazy<Boolean> help = Lazy.of(this::_isHelp);
    private final Lazy<String> command = Lazy.of(this::_getCommand);
    private final Lazy<CommandArguments> arguments = Lazy.of(this::_getArguments);
    private final Lazy<CommandResponder> responder = Lazy.of(this::_getResponder);

    private final Resolvers resolvers;
    private final PrefixService prefixService;

    public boolean isRelevantCommand() {
        return relevantCommand.get();
    }

    private boolean _isRelevantCommand() {
        if (getAuthor().isBot()) {
            log.debug("Ignoring bot message");
            return false;
        } else if (getPrefix().isPresent()) {
            log.info("Processing message with prefix");
            return true;
        } else {
            log.debug("Ignoring message without prefix");
            return false;
        }
    }

    public Optional<String> getPrefix() {
        return prefix.getOptional();
    }

    private String _getPrefix() {
        return prefixService.extractPrefix(event.getMessage());
    }

    public boolean isHelp() {
        return help.get();
    }

    private boolean _isHelp() {
        final String prefix = getPrefix().orElse("");
        final String input = getMessage().getContentRaw().substring(prefix.length()).trim();
        final Matcher helpMatcher = HELP_COMMAND_PATTERN.matcher(input);
        if (helpMatcher.matches()) {
            return true;
        }
        final Matcher helpFlagMatcher = HELP_FLAG_PATTERN.matcher(input);
        return helpFlagMatcher.find();
    }

    public Optional<String> getCommand() {
        return command.getOptional();
    }

    private String _getCommand() {
        final String prefix = getPrefix().orElse("");
        final String input = getMessage().getContentRaw().substring(prefix.length()).trim();
        final Matcher commandMatcher = COMMAND_PATTERN.matcher(input);
        if (commandMatcher.matches()) {
            return commandMatcher.group(1).toLowerCase();
        } else {
            return null;
        }
    }

    public CommandArguments getArguments() {
        return arguments.get();
    }

    private CommandArguments _getArguments() {
        return new CommandArguments(this);
    }

    public CommandResponder getResponder() {
        return responder.get();
    }

    private CommandResponder _getResponder() {
        return new CommandResponder(this);
    }

    // @Delegate

    public User getAuthor() {
        return event.getAuthor();
    }

    public TextChannel getChannel() {
        return event.getChannel();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public Member getMember() {
        return event.getMember();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public String getMessageId() {
        return event.getMessageId();
    }

}
