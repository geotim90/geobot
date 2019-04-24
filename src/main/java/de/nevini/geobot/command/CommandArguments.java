package de.nevini.geobot.command;

import net.dv8tion.jda.core.entities.TextChannel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandArguments {

    private static final Pattern ARGUMENTS_PATTERN = Pattern.compile("(?i)(?:help\\s+)?(?:\\w+)\\s+(.+)");

    private static final String MENTION_REGEX = "(?:(?:(?:--|//)\\w+|[-/]\\w)\\s+)?<(?:@&?|#)\\d+>";
    private static final String OPTION_REGEX = "(?:(?:--|//)\\w+|[-/]\\w)(?:\\s+.+)?";

    private static final Pattern DM_FLAG_PATTERN = Pattern.compile("(?i)(?:--|//)dm");
    private static final Pattern RM_FLAG_PATTERN = Pattern.compile("(?i)(?:--|//)rm");

    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("<#(\\d+)>");
    private static final Pattern CHANNEL_OPTION_PATTERN = Pattern.compile("(?i)(?:(?:--|//)channel|[-/]c)(?:\\s+(.+))?");

    private final CommandContext context;

    private final String argument;
    private final List<String> options;

    public CommandArguments(CommandContext context) {
        this.context = context;

        final List<String> arguments;
        final String prefix = context.getPrefix().orElse("");
        final String input = context.getMessage().getContentRaw().substring(prefix.length()).trim();
        final Matcher argumentsMatcher = ARGUMENTS_PATTERN.matcher(input);
        if (argumentsMatcher.matches()) {
            arguments = Arrays.asList(argumentsMatcher.group(1).split("\\s+(?=" + OPTION_REGEX + "|" + MENTION_REGEX + ")"));
        } else {
            arguments = Collections.emptyList();
        }

        final Optional<String> argument = arguments.stream().findFirst();
        if (argument.isPresent() && !argument.get().matches(OPTION_REGEX) && !argument.get().matches(MENTION_REGEX)) {
            this.argument = argument.get();
        } else {
            this.argument = null;
        }

        this.options = arguments.stream()
                .filter(s -> s.matches(OPTION_REGEX) || s.matches(MENTION_REGEX))
                .collect(Collectors.toList());
    }

    public Optional<String> getArgument() {
        return Optional.ofNullable(argument);
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public boolean hasFlag(String flag) {
        return hasFlag(Pattern.compile("(?i)(?:--|//)" + flag));
    }

    public boolean hasFlag(String flag, String code) {
        return hasFlag(Pattern.compile("(?i)(?:(?:--|//)" + flag + "|[-/]" + code + ")"));
    }

    public boolean hasFlag(Pattern pattern) {
        return options.stream().anyMatch(option -> pattern.matcher(option).matches());
    }

    public boolean hasAllFlag() {
        return hasFlag("all", "a");
    }

    public boolean hasDmFlag() {
        return hasFlag(DM_FLAG_PATTERN);
    }

    public boolean hasRmFlag() {
        return hasFlag(RM_FLAG_PATTERN);
    }

    public List<TextChannel> getChannelReferences(CommandContext context) {
        final ArrayList<TextChannel> references = new ArrayList<>();
        options.stream()
                .map(CHANNEL_OPTION_PATTERN::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> references.addAll(context.getResolvers().getChannelResolver().resolve(context, matcher.group(1))));
        options.stream()
                .map(CHANNEL_MENTION_PATTERN::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> references.addAll(context.getResolvers().getChannelResolver().resolve(context, matcher.group(1))));
        return references;
    }

}
