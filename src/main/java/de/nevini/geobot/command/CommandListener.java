package de.nevini.geobot.command;

import de.nevini.geobot.services.ModuleService;
import de.nevini.geobot.services.PermissionService;
import de.nevini.geobot.services.PrefixService;
import de.nevini.geobot.sharding.ShardEventListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.regex.Matcher;

@Slf4j
@ShardEventListener
public class CommandListener {

    private final Resolvers resolvers;
    private final PrefixService prefixService;
    private final ModuleService moduleService;
    private final PermissionService permissionService;

    private final Map<String, AbstractCommand> commands;

    public CommandListener(
            @Autowired ApplicationContext applicationContext,
            @Autowired Resolvers resolvers,
            @Autowired PrefixService prefixService,
            @Autowired ModuleService moduleService,
            @Autowired PermissionService permissionService
    ) {
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
        this.resolvers = resolvers;
        this.prefixService = prefixService;
        this.moduleService = moduleService;
        this.permissionService = permissionService;
    }

    @SubscribeEvent
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        final CommandContext context = new CommandContext(e, resolvers, prefixService);
        updateMDC(context);
        log.debug("Received message");
        if (context.isRelevantCommand()) {
            processCommand(context);
        }
        clearMDC();
    }

    private void processCommand(CommandContext context) {
        final Optional<String> keyword = context.getCommand();
        if (!keyword.isPresent()) {
            context.getResponder().replyNoCommand();
            return;
        }

        final AbstractCommand command = commands.get(keyword.get());
        if (command == null) {
            context.getResponder().replyInvalidCommand();
            return;
        }

        if (!moduleService.isModuleActive(context.getMessage().getGuild(), command.getModule())) {
            context.getResponder().replyInactiveModule();
            return;
        }

        final String input = context.getArguments().getArgument().orElse(StringUtils.EMPTY);
        for (CommandMatcher commandMatcher : command.getCommandMatchers()) {
            final Matcher matcher = commandMatcher.match(input);
            if (matcher.matches()) {
                if (permissionService.hasPermission(context.getChannel(), context.getMember(), commandMatcher.getNode())) {
                    if (context.isHelp()) {
                        commandMatcher.getHelpMethod().accept(context, matcher);
                    } else {
                        commandMatcher.getCommandMethod().accept(context, matcher);
                    }
                } else {
                    context.getResponder().replyMissingPermissions();
                }
                return;
            }
        }

        if (context.isHelp()) {
            command.onHelp(context);
        } else {
            context.getResponder().replyInvalidArgument();
        }
    }

    private void updateMDC(CommandContext context) {
        clearMDC();
        MDC.put("server", context.getGuild().getId());
        MDC.put("channel", context.getChannel().getId());
        MDC.put("user", context.getAuthor().getId());
        MDC.put("message", context.getMessageId());
    }

    private void clearMDC() {
        MDC.clear();
    }

}
