package de.nevini.geobot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CommandResponder {

    private static final int MAX_LENGTH = 2000;

    private static final String EMOTE_ERROR = "❌";
    private static final String EMOTE_FORBIDDEN = "⛔";
    private static final String EMOTE_INACTIVE = "\uD83D\uDEAB";
    private static final String EMOTE_OK = "✅";
    private static final String EMOTE_UNKNOWN = "❓";

    private final CommandContext context;

    public void replyOk(String content) {
        reply(content, EMOTE_OK);
    }

    public void replyError(String content) {
        reply(content, EMOTE_ERROR);
    }

    public void replyInvalidArgument() {
        reply("you did not enter a valid command. Use `help " + context.getCommand().orElse("") + "` for more information.", EMOTE_UNKNOWN);
    }

    public void replyInvalidCommand() {
        reply("you did not enter a valid command. Use `help` to see a list of commands you can use.", EMOTE_UNKNOWN);
    }

    public void replyInactiveModule() {
        reply("you cannot execute a command from an inactive module.", EMOTE_INACTIVE);
    }

    public void replyMissingPermissions() {
        reply("you do not have permission to execute this command.", EMOTE_FORBIDDEN);
    }

    public void replyNoCommand() {
        reply("you did not enter any command. Use `help` to see a list of commands you can use.", EMOTE_UNKNOWN);
    }

    public void replyAmbiguous(List<TextChannel> channels) {
        final StringBuilder sb = new StringBuilder("I do not know which of the following channels you mean...\n```css\n");
        channels.forEach(channel -> sb.append(channel.getId()).append(" - ").append(channel.getName()).append("\n"));
        sb.append("```");
        reply(sb.toString());
    }

    private void reply(String content, String reaction) {
        if (!canReply() && canReact()) {
            log.info("Adding reaction: {}", reaction);
            context.getMessage().addReaction(reaction).queue();
            replyDm(content);
        } else {
            reply(content);
        }
    }

    public void reply(String content) {
        if (canReply()) {
            final String reply = StringUtils.abbreviate(context.getMessage().getAuthor().getAsMention() + ", " + content, MAX_LENGTH);
            log.info("Replying with message: {}", reply.replace("\n", "\\n"));
            context.getChannel().sendMessage(reply).queue();
            checkRm();
        } else {
            replyDm(content);
        }
    }

    public void replyDm(String content) {
        final String reply = StringUtils.abbreviate(StringUtils.capitalize(content), MAX_LENGTH);
        log.info("Replying with direct message: {}", reply.replace("\n", "\\n"));
        context.getAuthor().openPrivateChannel().complete().sendMessage(reply).queue();
        checkRm();
    }

    private boolean canReply() {
        return context.getArguments().hasDmFlag()
                && PermissionUtil.checkPermission(context.getChannel(), context.getGuild().getSelfMember(),
                Permission.MESSAGE_READ, Permission.MESSAGE_WRITE);
    }

    private boolean canReact() {
        return !context.getArguments().hasDmFlag()
                && PermissionUtil.checkPermission(context.getChannel(), context.getGuild().getSelfMember(),
                Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION);
    }

    private void checkRm() {
        if (context.getArguments().hasRmFlag() && canDelete()) {
            log.info("Deleting message due to 'rm' flag");
            context.getMessage().delete().queue();
        }
    }

    private boolean canDelete() {
        return PermissionUtil.checkPermission(context.getChannel(), context.getGuild().getSelfMember(),
                Permission.MESSAGE_MANAGE);
    }

}
