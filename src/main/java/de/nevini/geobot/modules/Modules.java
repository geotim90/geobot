package de.nevini.geobot.modules;

import java.util.Arrays;
import java.util.List;

public final class Modules {

    public static final String CORE = "core";

    private Modules() {
        // no instances
    }

    public static List<String> list() {
        return Arrays.asList(
                CORE
        );
    }

}
