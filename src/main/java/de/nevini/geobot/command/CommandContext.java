package de.nevini.geobot.command;

import de.nevini.geobot.services.PrefixService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.data.util.Lazy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Value
public class CommandContext {

    private static final Pattern ARGUMENTS_PATTERN = Pattern.compile("(?i)(?:help\\s+)?(?:\\w+)\\s+(.+)");
    private static final Pattern COMMAND_PATTERN = Pattern.compile("(?i)(?:help\\s+)?(\\w+)(?:\\s+.+)?");
    private static final Pattern HELP_COMMAND_PATTERN = Pattern.compile("(?i)(?:help)(?:\\s+.+)?");
    private static final Pattern HELP_FLAG_PATTERN = Pattern.compile("(?i)(?:(?:--|//)help|[-/]\\?)");

    private static final String MENTION_REGEX = "(?:(?:(?:--|//)\\w+|[-/]\\w)\\s+)?<(?:@&?|#)\\d+>";
    private static final String OPTION_REGEX = "(?:(?:--|//)\\w+|[-/]\\w)(?:\\s+.+)?";

    private final GuildMessageReceivedEvent event;

    private final Lazy<Boolean> relevantCommand = Lazy.of(this::_isRelevantCommand);
    private final Lazy<String> prefix = Lazy.of(this::_getPrefix);
    private final Lazy<Boolean> help = Lazy.of(this::_isHelp);
    private final Lazy<String> command = Lazy.of(this::_getCommand);
    private final Lazy<List<String>> arguments = Lazy.of(this::_getArguments);
    private final Lazy<String> argument = Lazy.of(this::_getArgument);
    private final Lazy<List<String>> options = Lazy.of(this::_getOptions);

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

    private Optional<String> getPrefix() {
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

    private List<String> getArguments() {
        return arguments.orElse(Collections.emptyList());
    }

    private List<String> _getArguments() {
        final String prefix = getPrefix().orElse("");
        final String input = getMessage().getContentRaw().substring(prefix.length()).trim();
        final Matcher argumentsMatcher = ARGUMENTS_PATTERN.matcher(input);
        if (argumentsMatcher.matches()) {
            return Arrays.asList(argumentsMatcher.group(1).split("\\s+(?=" + OPTION_REGEX + "|" + MENTION_REGEX + ")"));
        } else {
            return null;
        }
    }

    public Optional<String> getArgument() {
        return argument.getOptional();
    }

    private String _getArgument() {
        final Optional<String> argument = getArguments().stream().findFirst();
        if (argument.isPresent() && !argument.get().matches(OPTION_REGEX) && !argument.get().matches(MENTION_REGEX)) {
            return argument.get();
        } else {
            return null;
        }
    }

    public List<String> getOptions() {
        return options.orElse(Collections.emptyList());
    }

    private List<String> _getOptions() {
        return getArguments()
                .stream()
                .filter(argument -> argument.matches(OPTION_REGEX) || argument.matches(MENTION_REGEX))
                .collect(Collectors.toList());
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
