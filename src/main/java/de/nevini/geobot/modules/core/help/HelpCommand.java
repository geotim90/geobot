package de.nevini.geobot.modules.core.help;

import de.nevini.geobot.messaging.AbstractCommand;
import de.nevini.geobot.messaging.CommandMatcher;
import de.nevini.geobot.messaging.MessageContext;
import de.nevini.geobot.messaging.Patterns;
import de.nevini.geobot.modules.Modules;
import de.nevini.geobot.modules.core.modules.ModulesHandler;
import de.nevini.geobot.modules.core.permissions.PermissionsHandler;
import lombok.NonNull;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class HelpCommand extends AbstractCommand {

    private static final String KEYWORD = "help";

    private final CommandMatcher helpMatcher = new CommandMatcher(
            Pattern.compile(".*"),
            getNode(),
            (context, matcher) -> onHelp(context),
            (context, matcher) -> onHelp(context)
    );

    private final ModulesHandler modulesHandler;
    private final PermissionsHandler permissionsHandler;
    private final ApplicationContext applicationContext;

    public HelpCommand(
            @Autowired @NonNull ModulesHandler modulesHandler,
            @Autowired @NonNull PermissionsHandler permissionsHandler,
            @Autowired @NonNull ApplicationContext applicationContext
    ) {
        super("Use the **help** command to find out which commands you can use and what they do.",
                Modules.CORE, KEYWORD);
        this.modulesHandler = modulesHandler;
        this.permissionsHandler = permissionsHandler;
        this.applicationContext = applicationContext;
    }

    @Override
    public List<CommandMatcher> getCommandMatchers() {
        return Collections.singletonList(
                helpMatcher
        );
    }

    @Override
    public void onHelp(MessageContext context) {
        if (context.getArgument().isPresent()) {
            doHelp(context);
        } else {
            doCommandList(context);
        }
    }

    private void doHelp(MessageContext context) {
        context.reply("use the **help** command to find out which commands you can use and what they do.\n" +
                "This is the only command that can be executed by sending a direct message to Geobot.\n" +
                "\n" +
                "__**Commands**__\n" +
                "**help** - Displays a list of commands available to the current user. The list is limited by the permissions of the current user in the current channel.\n" +
                "**help** \\<*command*\\> \\[*arguments*\\] - Displays a description of the *command* and how to use it if the current user has the approriate permissions in the current channel. *Arguments* may be provided to get more details on specific features.\n" +
                "\n" +
                "__**Options**__\n" +
                "--all - Lists all available commands regardless of the current scope. This option is active by default if the command is executed via direct message. Using this option will cause the reply to be sent via direct message.\n" +
                "--channel \\<*channel*\\> - Executes the command within the scope of the provided *channel*. Using this option will cause the reply to be sent via direct message.");
    }

    private void doCommandList(MessageContext context) {
        if (context.getMessage().getChannelType() == ChannelType.PRIVATE || !context.getOptions(Patterns.ALL_FLAG).isEmpty()) {
            doGlobalCommandList(context);
        } else {
            doChannelCommandList(context);
        }
    }

    private void doGlobalCommandList(MessageContext context) {
        final StringBuilder sb = new StringBuilder("here is a full list of commands that are supported by Geobot.\n\n");
        final Collection<AbstractCommand> availableCommands = applicationContext.getBeansOfType(AbstractCommand.class).values();
        for (String module : Modules.list()) {
            sb.append("__**").append(StringUtils.capitalize(module)).append("**__\n");
            availableCommands.stream()
                    .filter(command -> command.getModule().equals(module))
                    .sorted(Comparator.comparing(AbstractCommand::getKeyword))
                    .forEach(command -> sb.append(command.getDescription()).append("\n"));
            sb.append("\n\n");
        }
        context.replyDm(sb.toString());
    }

    private void doChannelCommandList(MessageContext context) {
        final List<TextChannel> channels = context.getChannelReferences();
        final TextChannel channel;
        if (channels.isEmpty()) {
            channel = context.getMessage().getTextChannel();
        } else if (channels.size() == 1) {
            channel = channels.get(0);
        } else {
            context.replyAmbiguous(channels);
            return;
        }
        final Guild guild = context.getMessage().getGuild();
        final StringBuilder sb = new StringBuilder("here is a list of commands that are available to you in the **")
                .append(channel.getName()).append("** channel of the **").append(guild.getName()).append("** server.\n\n");
        final Collection<AbstractCommand> availableCommands = applicationContext.getBeansOfType(AbstractCommand.class).values();
        for (String module : Modules.list()) {
            if (modulesHandler.isActive(guild, module)) {
                sb.append("__**").append(StringUtils.capitalize(module)).append("**__\n");
                availableCommands.stream()
                        .filter(command -> command.getModule().equals(module))
                        .filter(command -> permissionsHandler.hasPermission(channel, context.getMessage().getMember(), command.getNode()))
                        .sorted(Comparator.comparing(AbstractCommand::getKeyword))
                        .forEach(command -> sb.append(command.getDescription()).append("\n"));
                sb.append("\n\n");
            }
        }
        if (channels.isEmpty()) {
            context.reply(sb.toString());
        } else {
            context.replyDm(sb.toString());
        }
    }

}
