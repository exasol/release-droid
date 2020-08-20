package com.exasol;

import java.util.HashSet;
import java.util.Set;

/**
 * This class contains supported release platforms.
 */
public enum ReleasePlatform {
    GITHUB;

    /**
     * Create a set of {@link ReleasePlatform}s from Strings.
     * 
     * @param platforms one or more platform names as Strings
     * @return set of members of {@link ReleasePlatform} enum class
     */
    public static Set<ReleasePlatform> toSet(final String... platforms) {
        final Set<ReleasePlatform> platformsList = new HashSet<>();
        for (final String platform : platforms) {
            platformsList.add(ReleasePlatform.valueOf(platform.toUpperCase().trim()));
        }
        return platformsList;
    }
}