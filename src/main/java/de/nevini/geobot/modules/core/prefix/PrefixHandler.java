package de.nevini.geobot.modules.core.prefix;

import de.nevini.geobot.data.prefix.Prefix;
import de.nevini.geobot.data.prefix.PrefixRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class PrefixHandler {

    private final PrefixRepository prefixRepository;

    private final String prefixDefault;
    private final boolean prefixMention;
    private final String prefixName;

    public PrefixHandler(
            @Autowired @NonNull PrefixRepository prefixRepository,
            @Value("${geobot.prefix.default:G>}") String prefixDefault,
            @Value("${geobot.prefix.mention:true}") boolean prefixMention,
            @Value("${geobot.prefix.name:Geobot}") String prefixName
    ) {
        this.prefixRepository = prefixRepository;
        this.prefixDefault = prefixDefault;
        this.prefixMention = prefixMention;
        this.prefixName = prefixName;
    }

    /**
     * Extracts the prefix from the provided message, if a valid prefix is present.
     *
     * @return the prefix or {@code null} if no valid prefix is present.
     */
    public String extractPrefix(Message message) {
        final String content = message.getContentRaw();
        if (isMentionAllowed()) {
            final String mention = message.getJDA().getSelfUser().getAsMention();
            if (content.startsWith(mention)) {
                return mention;
            }
            final String mentionByName = '@' + getName(message);
            if (content.startsWith(mentionByName)) {
                return mentionByName;
            }
        }
        final String prefix = getPrefix(message);
        if (content.startsWith(prefix)) {
            return prefix;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the prefix relevant to the provided message.
     */
    public String getPrefix(Message message) {
        if (message.getChannelType() == ChannelType.TEXT) {
            return getGuildPrefix(message.getGuild());
        } else {
            return getDefaultPrefix();
        }
    }

    /**
     * Retrieves the prefix relevant to the provided guild.
     */
    private String getGuildPrefix(Guild guild) {
        final Optional<Prefix> prefix = prefixRepository.findById(guild.getIdLong());
        return prefix.isPresent() ? prefix.get().getPrefix() : prefixDefault;
    }

    /**
     * Retrieves the default prefix.
     */
    private String getDefaultPrefix() {
        return prefixDefault;
    }

    /**
     * Whether a mention counts as a valid prefix.
     */
    private boolean isMentionAllowed() {
        return prefixMention;
    }

    /**
     * Retrieves the effective name relevant to the provided message.
     */
    private String getName(Message message) {
        if (message.getChannelType() == ChannelType.TEXT) {
            return getGuildName(message.getGuild());
        } else {
            return getDefaultName();
        }
    }

    /**
     * Retrieves the effective name relevant to the provided guild.
     */
    private String getGuildName(Guild guild) {
        return guild.getSelfMember().getEffectiveName();
    }

    /**
     * Retrieves the default name.
     */
    private String getDefaultName() {
        return prefixName;
    }

    /**
     * Configures the prefix for the provided guild.
     */
    public void setGuildPrefix(Guild guild, String prefix) {
        Prefix bean = new Prefix(guild.getIdLong(), prefix);
        log.info("Storing bean: " + bean.toString());
        prefixRepository.save(bean);
    }

}
