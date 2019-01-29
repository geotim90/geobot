package de.nevini.geobot.messaging;

import de.nevini.geobot.sharding.ShardEventListener;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

@Slf4j
@ShardEventListener
public class CommandListener {

    private static final String HELP_KEYWORD = "help";

    private final MessageHandlers messageHandlers;

    private final Map<String, AbstractCommand> commands;

    public CommandListener(
            @Autowired @NonNull MessageHandlers messageHandlers,
            @Autowired ApplicationContext applicationContext
    ) {
        this.messageHandlers = messageHandlers;

        final Map<String, AbstractCommand> commands = new HashMap<>();
        final Collection<AbstractCommand> availableCommands = applicationContext.getBeansOfType(AbstractCommand.class).values();
        for (AbstractCommand command : availableCommands) {
            for (String keyword : command.getKeywords()) {
                final AbstractCommand conflictingCommand = commands.put(keyword.toLowerCase(), command);
                if (conflictingCommand == null) {
                    log.info("Registered command {} for keyword: {}", command.getClass().getSimpleName(), keyword);
                } else {
                    log.warn("Conflicting commands for keyword: {} ({} was overwritten by {})", keyword, conflictingCommand.getClass().getSimpleName(), command.getClass().getSimpleName());
                }
            }
        }
        this.commands = Collections.unmodifiableMap(commands);
    }

    @SubscribeEvent
    public void onGuildMessage(GuildMessageReceivedEvent event) {
        log.info("Received guild message: {}", event.getMessage().getId());
        final MessageContext context = new MessageContext(messageHandlers, event.getMessage());

        if (!context.isRelevant()) {
            return;
        }

        if (!context.isValidChannel()) {
            context.replyInvalidChannel();
            return;
        }

        final String keyword = context.getCommand().orElse(HELP_KEYWORD);
        final AbstractCommand command = commands.get(keyword);
        if (command == null) {
            context.replyInvalidCommand();
            return;
        }

        if (!context.isHelp() && !messageHandlers.getModulesHandler().isActive(context.getMessage().getGuild(), command.getModule())) {
            context.replyInactiveModule();
            return;
        }

        final String input = context.getArgument().orElse(StringUtils.EMPTY);
        for (CommandMatcher commandMatcher : command.getCommandMatchers()) {
            final Matcher matcher = commandMatcher.match(input);
            if (matcher.matches()) {
                if (messageHandlers.getPermissionsHandler().hasPermission(context, commandMatcher.getNode())) {
                    if (context.isHelp()) {
                        commandMatcher.getHelpMethod().accept(context, matcher);
                    } else {
                        commandMatcher.getCommandMethod().accept(context, matcher);
                    }
                } else {
                    context.replyMissingPermissions();
                }
                return;
            }
        }

        if (context.isHelp()) {
            command.onHelp(context);
        } else {
            context.replyInvalidCommand();
        }
    }

}
