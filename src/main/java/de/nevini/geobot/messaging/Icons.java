package de.nevini.geobot.messaging;

public final class Icons {

    private static final String CROSS_MARK = "❌";
    private static final String NO_ENTRY = "⛔";
    private static final String PROHIBITED = "\uD83D\uDEAB";
    private static final String QUESTION_MARK = "❓";
    private static final String WHITE_HEAVY_CHECK_MARK = "✅";

    public static final String OK = WHITE_HEAVY_CHECK_MARK;
    public static final String ERROR = CROSS_MARK;
    public static final String INVALID_CHANNEL = NO_ENTRY;
    public static final String INVALID_COMMAND = QUESTION_MARK;
    public static final String MISSING_PERMISSIONS = NO_ENTRY;
    public static final String MODULE_ACTIVE = WHITE_HEAVY_CHECK_MARK;
    public static final String MODULE_INACTIVE = PROHIBITED;

    private Icons() {
        // no instances
    }

}
