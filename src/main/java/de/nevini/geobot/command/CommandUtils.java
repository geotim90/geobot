package de.nevini.geobot.command;

import java.util.regex.Pattern;

public final class CommandUtils {

    private static final Pattern DM_FLAG_PATTERN = Pattern.compile("(?i)(?:--|//)dm");
    private static final Pattern RM_FLAG_PATTERN = Pattern.compile("(?i)(?:--|//)rm");

    public static boolean hasDmFlag(CommandContext context) {
        return context.getOptions().stream().anyMatch(option -> DM_FLAG_PATTERN.matcher(option).matches());
    }

    public static boolean hasNoDmFlag(CommandContext context) {
        return context.getOptions().stream().noneMatch(option -> DM_FLAG_PATTERN.matcher(option).matches());
    }

    public static boolean hasRmFlag(CommandContext context) {
        return context.getOptions().stream().anyMatch(option -> RM_FLAG_PATTERN.matcher(option).matches());
    }

}
