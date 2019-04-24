package de.nevini.geobot.command;

import de.nevini.geobot.data.game.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandArguments {

    private static final Pattern ARGUMENTS_PATTERN = Pattern.compile("(?i)(?:help\\s+)?(?:\\w+)\\s+(.+)");

    private static final String MENTION_REGEX = "(?:(?:(?:--|//)\\w+|[-/]\\w)\\s+)?<(?:@&?|#)\\d+>";
    private static final String OPTION_REGEX = "(?:(?:--|//)\\w+|[-/]\\w)(?:\\s+.+)?";

    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("<#(\\d+)>");
    private static final Pattern CHANNEL_OPTION_PATTERN = Pattern.compile("(?i)(?:(?:--|//)channel|[-/]c)\\s+(.+)");
    private static final Pattern GAME_OPTION_PATTERN = Pattern.compile("(?i)(?:--|//)game\\s+(.+)");
    private static final Pattern NODE_OPTION_PATTERN = Pattern.compile("(?i)(?:(?:--|//)node|[-/]n)\\s+(.+)");
    private static final Pattern ROLE_MENTION_PATTERN = Pattern.compile("<@&(\\d+)>");
    private static final Pattern ROLE_OPTION_PATTERN = Pattern.compile("(?i)(?:(?:--|//)role|[-/]r)\\s+(.+)");
    private static final Pattern USER_MENTION_PATTERN = Pattern.compile("<@(\\d+)>");
    private static final Pattern USER_OPTION_PATTERN = Pattern.compile("(?i)(?:(?:--|//)(?:user|member)|[-/][um])\\s+(.+)");

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

    private <T> List<T> getReferences(Pattern pattern, Resolver<T> resolver) {
        final List<T> references = new ArrayList<>();
        options.stream()
                .map(GAME_OPTION_PATTERN::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> references.addAll(resolver.resolve(context, matcher.group(1))));
        return references;
    }

    public List<TextChannel> getChannelReferences() {
        return ListUtils.union(
                getReferences(CHANNEL_MENTION_PATTERN, context.getResolvers().getChannelResolver()),
                getReferences(CHANNEL_OPTION_PATTERN, context.getResolvers().getChannelResolver())
        );
    }

    public List<Game> getGameReferences() {
        return getReferences(GAME_OPTION_PATTERN, context.getResolvers().getGameResolver());
    }

    public List<String> getNodeReferences() {
        return getReferences(NODE_OPTION_PATTERN, context.getResolvers().getNodeResolver());
    }

    public List<Role> getRoleReferences() {
        return ListUtils.union(
                getReferences(ROLE_MENTION_PATTERN, context.getResolvers().getRoleResolver()),
                getReferences(ROLE_OPTION_PATTERN, context.getResolvers().getRoleResolver())
        );
    }

    public List<Member> getUserReferences() {
        return ListUtils.union(
                getReferences(USER_MENTION_PATTERN, context.getResolvers().getUserResolver()),
                getReferences(USER_OPTION_PATTERN, context.getResolvers().getUserResolver())
        );
    }

    private boolean hasFlag(String flag) {
        return hasFlag(Pattern.compile("(?i)(?:--|//)" + flag));
    }

    private boolean hasFlag(String flag, String code) {
        return hasFlag(Pattern.compile("(?i)(?:(?:--|//)" + flag + "|[-/]" + code + ")"));
    }

    private boolean hasFlag(Pattern pattern) {
        return options.stream().anyMatch(option -> pattern.matcher(option).matches());
    }

    public boolean hasDmFlag() {
        return hasFlag("dm");
    }

    public boolean hasHelpFlag() {
        return hasFlag("help", "?");
    }

    public boolean hasRmFlag() {
        return hasFlag("rm");
    }

    public boolean hasAllFlag() {
        return hasFlag("all", "a");
    }

    public boolean hasChannelFlag() {
        return hasFlag("channel", "c");
    }

    public boolean hasServerFlag() {
        return hasFlag("server", "s") || hasFlag("guild", "g");
    }

    public boolean hasUserFlag() {
        return hasFlag("user", "u") || hasFlag("member", "m");
    }

}
