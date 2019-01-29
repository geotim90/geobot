package de.nevini.geobot.services;

import de.nevini.geobot.data.prefix.Prefix;
import de.nevini.geobot.data.prefix.PrefixRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PrefixService {

    private final PrefixRepository prefixRepository;

    private final String prefixDefault;
    private final boolean prefixMention;
    private final String prefixName;

    public PrefixService(
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

    public String extractPrefix(Message message) {
        final String content = message.getContentRaw();
        if (isMentionAllowed()) {
            final String mention = getSelfMention(message);
            if (content.startsWith(mention)) {
                return mention;
            }
            final String mentionByName = '@' + getSelfName(message);
            if (content.startsWith(mentionByName)) {
                return mentionByName;
            }
        }
        final String prefix = getServerPrefix(message.getGuild());
        if (content.startsWith(prefix)) {
            return prefix;
        } else {
            return null;
        }
    }

    private boolean isMentionAllowed() {
        return prefixMention;
    }

    private String getSelfMention(Message message) {
        return message.getJDA().getSelfUser().getAsMention();
    }

    private String getSelfName(Message message) {
        return StringUtils.defaultString(message.getGuild().getSelfMember().getEffectiveName(), prefixName);
    }

    public String getServerPrefix(Guild server) {
        final Optional<Prefix> data = prefixRepository.findById(server.getIdLong());
        return data.isPresent() ? data.get().getPrefix() : prefixDefault;
    }

    public void setServerPrefix(Guild server, String prefix) {
        Prefix data = new Prefix(server.getIdLong(), prefix);
        log.info("Storing data: {}", data);
        prefixRepository.save(data);
    }

}
