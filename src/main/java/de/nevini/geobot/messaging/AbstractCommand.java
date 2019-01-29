package de.nevini.geobot.messaging;

import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
public abstract class AbstractCommand {

    private final String description;
    private final String module;
    private final Collection<String> keywords;

    protected AbstractCommand(@NonNull String description, @NonNull String module, @NonNull String... keywords) {
        this.description = description;
        this.module = module;
        this.keywords = Arrays.asList(keywords);
    }

    public String getKeyword() {
        return keywords.stream().findFirst().orElse(null);
    }

    public String getNode() {
        return module + '.' + getKeyword();
    }

    public abstract List<CommandMatcher> getCommandMatchers();

    public abstract void onHelp(MessageContext context);

}
