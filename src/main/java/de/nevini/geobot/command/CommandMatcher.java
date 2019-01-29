package de.nevini.geobot.command;

import lombok.Value;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
public class CommandMatcher {

    private final Pattern pattern;
    private final String node;
    private final BiConsumer<CommandContext, Matcher> commandMethod;
    private final BiConsumer<CommandContext, Matcher> helpMethod;

    public Matcher match(String input) {
        return pattern.matcher(input);
    }

}
