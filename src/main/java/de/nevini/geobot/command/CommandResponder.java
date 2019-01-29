package de.nevini.geobot.command;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public final class CommandResponder {

    private static final int MAX_LENGTH = 2000;

    private static final String EMOTE_ERROR = "❌";
    private static final String EMOTE_FORBIDDEN = "⛔";
    private static final String EMOTE_INACTIVE = "\uD83D\uDEAB";
    private static final String EMOTE_OK = "✅";
    private static final String EMOTE_UNKNOWN = "❓";

    public static void replyOk(CommandContext context, String content) {
        reply(context, content, EMOTE_OK);
    }

    public static void replyError(CommandContext context, String content) {
        reply(context, content, EMOTE_ERROR);
    }

    public static void replyInvalidArgument(CommandContext context) {
        reply(context, "you did not enter a valid command. Use `help " + context.getCommand().orElse("") + "` for more information.", EMOTE_UNKNOWN);
    }

    public static void replyInvalidCommand(CommandContext context) {
        reply(context, "you did not enter a valid command. Use `help` to see a list of commands you can use.", EMOTE_UNKNOWN);
    }

    public static void replyInactiveModule(CommandContext context) {
        reply(context, "you cannot execute a command from an inactive module.", EMOTE_INACTIVE);
    }

    public static void replyMissingPermissions(CommandContext context) {
        reply(context, "you do not have permission to execute this command.", EMOTE_FORBIDDEN);
    }

    public static void replyNoCommand(CommandContext context) {
        reply(context, "you did not enter any command. Use `help` to see a list of commands you can use.", EMOTE_UNKNOWN);
    }

    private static void reply(CommandContext context, String content, String reaction) {
        if (!canReply(context) && canReact(context)) {
            log.info("Adding reaction: {}", reaction);
            context.getMessage().addReaction(reaction).queue();
            replyDm(context, content);
        } else {
            reply(context, content);
        }
    }

    public static void reply(CommandContext context, String content) {
        if (canReply(context)) {
            final String reply = StringUtils.abbreviate(context.getMessage().getAuthor().getAsMention() + ", " + content, MAX_LENGTH);
            log.info("Replying with message: {}", reply.replace("\n", "\\n"));
            context.getChannel().sendMessage(reply).queue();
            checkRm(context);
        } else {
            replyDm(context, content);
        }
    }

    public static void replyDm(CommandContext context, String content) {
        final String reply = StringUtils.abbreviate(StringUtils.capitalize(content), MAX_LENGTH);
        log.info("Replying with direct message: {}", reply.replace("\n", "\\n"));
        context.getAuthor().openPrivateChannel().complete().sendMessage(reply).queue();
        checkRm(context);
    }

    private static boolean canReply(CommandContext context) {
        return CommandUtils.hasDmFlag(context)
                && PermissionUtil.checkPermission(context.getChannel(), context.getGuild().getSelfMember(),
                Permission.MESSAGE_READ, Permission.MESSAGE_WRITE);
    }

    private static boolean canReact(CommandContext context) {
        return CommandUtils.hasNoDmFlag(context)
                && PermissionUtil.checkPermission(context.getChannel(), context.getGuild().getSelfMember(),
                Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION);
    }

    private static void checkRm(CommandContext context) {
        if (CommandUtils.hasRmFlag(context) && canDelete(context)) {
            log.info("Deleting message due to 'rm' flag");
            context.getMessage().delete().queue();
        }
    }

    private static boolean canDelete(CommandContext context) {
        return PermissionUtil.checkPermission(context.getChannel(), context.getGuild().getSelfMember(),
                Permission.MESSAGE_MANAGE);
    }

}
