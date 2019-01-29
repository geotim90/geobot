package de.nevini.geobot.modules.core.resolve;

import de.nevini.geobot.data.game.Game;
import de.nevini.geobot.messaging.AbstractCommand;
import de.nevini.geobot.messaging.CommandMatcher;
import de.nevini.geobot.messaging.MessageContext;
import de.nevini.geobot.modules.Modules;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ResolveCommand extends AbstractCommand {

    private static final String KEYWORD = "resolve";

    private final CommandMatcher resolveMatcher = new CommandMatcher(
            Pattern.compile(".*"),
            getNode(),
            (context, matcher) -> onResolve(context),
            (context, matcher) -> onHelp(context)
    );

    public ResolveCommand() {
        super("Use the **resolve** command to find IDs, mentions and names for various types of references.",
                Modules.CORE, KEYWORD);
    }

    @Override
    public List<CommandMatcher> getCommandMatchers() {
        return Collections.singletonList(
                resolveMatcher
        );
    }

    private void onResolve(MessageContext context) {
        if (!resolveChannels(context)
                && !resolveGames(context)
                && !resolveNodes(context)
                && !resolveRoles(context)
                && !resolveUsers(context)
        ) {
            context.reply("I could not find anything that matches your input...");
        }
    }

    private boolean resolveChannels(MessageContext context) {
        List<TextChannel> channels = context.getChannelReferences();
        if (channels.size() == 1) {
            final TextChannel channel = channels.get(0);
            context.reply("the channel you provided is " + channel.getAsMention() + ", which has the ID " + channel.getId() + ".");
            return true;
        } else if (!channels.isEmpty()) {
            StringBuilder sb = new StringBuilder("the channel reference(s) you provided matches " + channels.size() + " channels.\n");
            channels.forEach(channel -> sb.append("\n").append(channel.getAsMention()).append(", which has the ID ").append(channel.getId()));
            context.reply(sb.toString());
            return true;
        } else {
            return false;
        }
    }

    private boolean resolveGames(MessageContext context) {
        List<Game> games = context.getGameReferences();
        if (games.size() == 1) {
            final Game game = games.get(0);
            context.reply("the game you provided is **" + game.getName() + "**, which has the ID " + Long.toUnsignedString(game.getId()));
            return true;
        } else if (!games.isEmpty()) {
            StringBuilder sb = new StringBuilder("the game reference(s) you provided matches " + games.size() + " games.\n");
            games.forEach(game -> sb.append("\n**").append(game.getName()).append("**, which has the ID").append(Long.toUnsignedString(game.getId())));
            context.reply(sb.toString());
            return true;
        } else {
            return false;
        }
    }

    private boolean resolveNodes(MessageContext context) {
        List<String> nodes = context.getNodeReferences();
        if (nodes.size() == 1) {
            final String node = nodes.get(0);
            context.reply("the node you provided is **" + node + "**.");
            return true;
        } else if (!nodes.isEmpty()) {
            StringBuilder sb = new StringBuilder("the node reference(s) you provided matches " + nodes.size() + " nodes.\n");
            nodes.forEach(node -> sb.append("\n**").append(node).append("**"));
            context.reply(sb.toString());
            return true;
        } else {
            return false;
        }
    }

    private boolean resolveRoles(MessageContext context) {
        List<Role> roles = context.getRoleReferences();
        if (roles.size() == 1) {
            final Role role = roles.get(0);
            context.reply("the role you provided is **" + role.getName() + "**, which has the ID " + role.getId() + ".");
            return true;
        } else if (!roles.isEmpty()) {
            StringBuilder sb = new StringBuilder("the role reference(s) you provided matches " + roles.size() + " roles.\n");
            roles.forEach(role -> sb.append("\n**").append(role.getName()).append("**, which has the ID ").append(role.getId()).append("."));
            context.reply(sb.toString());
            return true;
        } else {
            return false;
        }
    }

    private boolean resolveUsers(MessageContext context) {
        List<Member> users = context.getUserReferences();
        if (users.size() == 1) {
            final Member user = users.get(0);
            context.reply("the user you provided is **" + user.getEffectiveName() + "**, who has the ID " + user.getUser().getId() + ".");
            return true;
        } else if (!users.isEmpty()) {
            StringBuilder sb = new StringBuilder("the user reference(s) you provided matches " + users.size() + " users.\n");
            users.forEach(user -> sb.append("\n**").append(user.getEffectiveName()).append("**, who has the ID ").append(user.getUser().getId()).append("."));
            context.reply(sb.toString());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onHelp(MessageContext context) {
        context.reply("Use the **resolve** command to find IDs, mentions and names for various types of references.\n" +
                "\n" +
                "__**Command**__\n" +
                "**resolve** \\<*reference*\\> - Attempts to interpret the provided *reference* and displays some basic information related to it.\n" +
                "\n" +
                "__**Options**__\n" +
                "--channel \\<*reference*\\> - Attempts to interpret the provided *reference* as a channel.\n" +
                "--game \\<*reference*\\> - Attempts to interpret the provided *reference* as a game.\n" +
                "--node \\<*reference*\\> - Attempts to interpret the provided *reference* as a permission node.\n" +
                "--role \\<*reference*\\> - Attempts to interpret the provided *reference* as a role.\n" +
                "--user \\<*reference*\\> - Attempts to interpret the provided *reference* as a user.");
    }

}