package de.nevini.geobot.messaging;

import de.nevini.geobot.data.game.Game;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Lazy;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class MessageContext {

    private static final int MAX_LENGTH = 2000;

    private final MessageHandlers handlers;

    @Getter
    private final Message message;

    private final Lazy<Boolean> relevant = Lazy.of(this::_isRelevant);
    private final Lazy<String> prefix = Lazy.of(this::_getPrefix);
    private final Lazy<Boolean> help = Lazy.of(this::_isHelp);
    private final Lazy<String> command = Lazy.of(this::_getCommand);
    private final Lazy<List<String>> arguments = Lazy.of(this::_getArguments);

    /**
     * Whether this message is relevant for command processing.
     */
    public boolean isRelevant() {
        return relevant.get();
    }

    private boolean _isRelevant() {
        if (message.getAuthor().isBot()) {
            log.info("Ignoring bot message: {}", message.getId());
            return false;
        } else if (message.getChannelType() == ChannelType.PRIVATE) {
            log.info("Processing direct message: {}", message.getId());
            return true;
        } else if (getPrefix().isPresent()) {
            log.info("Processing guild message: {}", message.getId());
            return true;
        } else {
            log.info("Ignoring guild message without prefix: {}", message.getId());
            return false;
        }
    }

    /**
     * Whether this message was sent in the correct context.
     */
    public boolean isValidChannel() {
        return message.getChannelType() == ChannelType.TEXT;
    }

    /**
     * Retrieves the prefix, if present.
     */
    public Optional<String> getPrefix() {
        return prefix.getOptional();
    }

    private String _getPrefix() {
        return handlers.getPrefixHandler().extractPrefix(message);
    }

    /**
     * Whether this command is a help command or has a help flag.
     */
    public boolean isHelp() {
        return help.get();
    }

    private boolean _isHelp() {
        final String prefix = getPrefix().orElse("");
        final String input = message.getContentRaw().substring(prefix.length()).trim();
        final Matcher helpMatcher = Patterns.HELP.matcher(input);
        if (helpMatcher.matches()) {
            return true;
        }
        final Matcher helpFlagMatcher = Patterns.HELP_FLAG.matcher(input);
        return helpFlagMatcher.find();
    }

    /**
     * Retrieves the command keyword, if present.
     */
    public Optional<String> getCommand() {
        return command.getOptional();
    }

    private String _getCommand() {
        final String prefix = getPrefix().orElse("");
        final String input = message.getContentRaw().substring(prefix.length()).trim();
        final Matcher commandMatcher = Patterns.COMMAND.matcher(input);
        if (commandMatcher.matches()) {
            return commandMatcher.group(1).toLowerCase();
        } else {
            return null;
        }
    }

    /**
     * Retrieves the command arguments, if present.
     *
     * @return a list of arguments or an empty list
     */
    private List<String> getArguments() {
        return arguments.orElse(Collections.emptyList());
    }

    private List<String> _getArguments() {
        final String prefix = getPrefix().orElse("");
        final String input = message.getContentRaw().substring(prefix.length()).trim();
        final Matcher argumentsMatcher = Patterns.ARGUMENTS.matcher(input);
        if (argumentsMatcher.matches()) {
            return Arrays.asList(argumentsMatcher.group(1).split("\\s+(?=" + Patterns.OPTION + "|" + Patterns.MENTION + ")"));
        } else {
            return null;
        }
    }

    /**
     * Retrieves the command argument, if present.
     */
    public Optional<String> getArgument() {
        final Optional<String> argument = getArguments().stream().findFirst();
        if (argument.isPresent() && (argument.get().matches(Patterns.OPTION) || argument.get().matches(Patterns.MENTION))) {
            return Optional.empty();
        } else {
            return argument;
        }
    }

    /**
     * Retrieves all command options, if present.
     *
     * @return a list of options or an empty list
     */
    public List<String> getOptions() {
        return getArguments()
                .stream()
                .filter(argument -> argument.matches(Patterns.OPTION) || argument.matches(Patterns.MENTION))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves certain command options, if present.
     *
     * @param pattern the {@link Pattern} to filter with
     * @return a list of options or an empty list
     */
    public List<String> getOptions(Pattern pattern) {
        return getArguments().stream().filter(argument -> pattern.matcher(argument).matches()).collect(Collectors.toList());
    }

    public List<TextChannel> getChannelReferences() {
        final ArrayList<TextChannel> refs = new ArrayList<>();
        getArguments().stream()
                .map(Patterns.CHANNEL_OPTION::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> refs.addAll(handlers.getChannelResolver().resolve(this, matcher.group(1))));
        getArguments().stream()
                .map(Patterns.CHANNEL_MENTION::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> refs.addAll(handlers.getChannelResolver().resolve(this, matcher.group(1))));
        return refs;
    }

    public List<Game> getGameReferences() {
        final ArrayList<Game> refs = new ArrayList<>();
        getArguments().stream()
                .map(Patterns.GAME_OPTION::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> refs.addAll(handlers.getGameResolver().resolve(this, matcher.group(1))));
        return refs;
    }

    public List<String> getNodeReferences() {
        final ArrayList<String> refs = new ArrayList<>();
        getArguments().stream()
                .map(Patterns.NODE_OPTION::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> refs.addAll(handlers.getNodeResolver().resolve(this, matcher.group(1))));
        return refs;
    }

    public List<Role> getRoleReferences() {
        final ArrayList<Role> refs = new ArrayList<>();
        getArguments().stream()
                .map(Patterns.ROLE_OPTION::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> refs.addAll(handlers.getRoleResolver().resolve(this, matcher.group(1))));
        getArguments().stream()
                .map(Patterns.ROLE_MENTION::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> refs.addAll(handlers.getRoleResolver().resolve(this, matcher.group(1))));
        return refs;
    }

    public List<Member> getUserReferences() {
        final ArrayList<Member> refs = new ArrayList<>();
        getArguments().stream()
                .map(Patterns.USER_OPTION::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> refs.addAll(handlers.getUserResolver().resolve(this, matcher.group(1))));
        getArguments().stream()
                .map(Patterns.USER_MENTION::matcher)
                .filter(Matcher::matches)
                .forEach(matcher -> refs.addAll(handlers.getUserResolver().resolve(this, matcher.group(1))));
        return refs;
    }

    public void replyOk(String content) {
        reply(content, Icons.OK);
    }

    public void replyError(String content) {
        reply(content, Icons.ERROR);
    }

    public void replyInvalidCommand() {
        reply("you did not enter a valid command. Use `help` to see a list of commands you can use.", Icons.INVALID_COMMAND);
    }

    public void replyInvalidChannel() {
        reply("you cannot execute that command here.", Icons.INVALID_CHANNEL);
    }

    public void replyInactiveModule() {
        reply("you cannot execute a command from an inactive module.", Icons.MODULE_INACTIVE);
    }

    public void replyMissingPermissions() {
        reply("you do not have permission to execute this command.", Icons.MISSING_PERMISSIONS);
    }

    public void replyAmbiguous(List<TextChannel> channels) {
        final StringBuilder sb = new StringBuilder("I do not know which of the following channels you mean...\n```css\n");
        channels.forEach(channel -> sb.append(channel.getId()).append(" - ").append(channel.getName()).append("\n"));
        sb.append("```");
        reply(sb.toString());
    }

    /**
     * Replies to the message using the most appropriate method available.
     * <ol>
     * <li>Reply by posting a message in the current channel.</li>
     * <li>React to the message in the current channel.</li>
     * <li>Send a direct message to the author.</li>
     * </ol>
     *
     * @param content  the content for 1 and 3
     * @param reaction the reaction for 2
     */
    private void reply(String content, String reaction) {
        if (!canReply() && canReact()) {
            log.info("Reacting to message {} with: {}", content, reaction);
            message.addReaction(reaction).queue();
            afterReply();
        } else {
            reply(content);
        }
    }

    /**
     * Replies to the message using the most appropriate method available.
     * <ol>
     * <li>Reply by posting a message in the current channel.</li>
     * <li>Send a direct message to the author.</li>
     * </ol>
     */
    public void reply(String content) {
        if (canReply()) {
            final String reply = StringUtils.abbreviate(message.getAuthor().getAsMention() + ", " + content, MAX_LENGTH);
            log.info("Replying to message {} in channel with: {}", message.getId(), reply.replace("\n", "\\n"));
            message.getChannel().sendMessage(reply).queue();
            afterReply();
        } else {
            replyDm(content);
        }
    }

    /**
     * Replies to the message via direct message.
     */
    public void replyDm(String content) {
        final String reply = StringUtils.abbreviate(StringUtils.capitalize(content), MAX_LENGTH);
        log.info("Replying to message {} with direct message: {}", message.getId(), reply.replace("\n", "\\n"));
        message.getAuthor().openPrivateChannel().complete().sendMessage(reply).queue();
        afterReply();
    }

    private boolean canReply() {
        return message.getChannelType() == ChannelType.TEXT
                && getOptions(Patterns.DM_FLAG).isEmpty()
                && PermissionUtil.checkPermission(
                message.getTextChannel(), message.getGuild().getSelfMember(),
                Permission.MESSAGE_READ, Permission.MESSAGE_WRITE);
    }

    private boolean canReact() {
        return message.getChannelType() == ChannelType.TEXT
                && getOptions(Patterns.DM_FLAG).isEmpty()
                && PermissionUtil.checkPermission(
                message.getTextChannel(), message.getGuild().getSelfMember(),
                Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION);
    }

    private void afterReply() {
        if (!getOptions(Patterns.RM_FLAG).isEmpty()) {
            log.info("Attempting to delete message {} due to 'rm' flag", message.getId());
            if (canDelete()) {
                message.delete().queue();
                log.info("Deleted message {}", message.getId());
            } else {
                log.info("Could not delete message {}", message.getId());
            }
        }
    }

    private boolean canDelete() {
        return message.getChannelType() == ChannelType.TEXT
                && PermissionUtil.checkPermission(
                message.getTextChannel(), message.getGuild().getSelfMember(),
                Permission.MESSAGE_MANAGE);
    }

}
