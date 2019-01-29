package de.nevini.geobot.messaging;

import java.util.regex.Pattern;

public final class Patterns {

    public static final Pattern ARGUMENTS = Pattern.compile("(?i)(?:help\\s+)?(?:\\w+)\\s+(.+)");
    public static final Pattern COMMAND = Pattern.compile("(?i)(?:help\\s+)?(\\w+)(?:\\s+.+)?");
    public static final Pattern HELP = Pattern.compile("(?i)(?:help)(?:\\s+.+)?");

    public static final String GET = "(?i)(?:get|display|list|show)";
    public static final String MENTION = "(?:(?:(?:--|//)\\w+|[-/]\\w)\\s+)?<(?:@&?|#)\\d+>";
    public static final String OPTION = "(?:(?:--|//)\\w+|[-/]\\w)(?:\\s+.+)?";

    public static final Pattern ALL_FLAG = Pattern.compile("(?i)(?:(?:--|//)all|[-/]a)");
    public static final Pattern CHANNEL_MENTION = Pattern.compile("<#(\\d+)>");
    public static final Pattern CHANNEL_OPTION = Pattern.compile("(?i)(?:(?:--|//)channel|[-/]c)(?:\\s+(.+))?");
    public static final Pattern DM_FLAG = Pattern.compile("(?i)(?:--|//)dm");
    public static final Pattern GAME_OPTION = Pattern.compile("(?i)(?:--|//)game\\s+(.+)");
    public static final Pattern HELP_FLAG = Pattern.compile("(?i)(?:(?:--|//)help|[-/]\\?)");
    public static final Pattern NODE_OPTION = Pattern.compile("(?i)(?:(?:--|//)node|[-/]n)\\s+(.+)");
    public static final Pattern RM_FLAG = Pattern.compile("(?i)(?:--|//)rm");
    public static final Pattern ROLE_MENTION = Pattern.compile("<@&(\\d+)>");
    public static final Pattern ROLE_OPTION = Pattern.compile("(?i)(?:(?:--|//)role|[-/]r)\\s+(.+)");
    public static final Pattern SERVER_FLAG = Pattern.compile("(?i)(?:(?:--|//)(?:server|guild)|[-/][sg])");
    public static final Pattern USER_MENTION = Pattern.compile("<@(\\d+)>");
    public static final Pattern USER_OPTION = Pattern.compile("(?i)(?:(?:--|//)(?:user|member)|[-/][um])(?:\\s+(.+))?");

    private Patterns() {
        // no instances
    }

}
