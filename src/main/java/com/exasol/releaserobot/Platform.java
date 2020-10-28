package com.exasol.releaserobot;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Marker for classes representing platforms. As all the platforms are unique, there are no common methods here.
 */
public interface Platform {
    /**
     * Get a name of the platform this class represents.
     * 
     * @return name from the {@link PlatformName}s
     */
    public PlatformName getPlatformName();

    /**
     * This class contains supported release platforms.
     */
    public enum PlatformName {
        GITHUB, MAVEN;

        /**
         * Create a set of {@link PlatformName}s from Strings.
         *
         * @param platforms one or more platform names as Strings
         * @return set of members of {@link PlatformName} enum class
         */
        public static Set<PlatformName> toSet(final String... platforms) {
            final Set<PlatformName> platformsList = new HashSet<>();
            for (final String platform : platforms) {
                platformsList.add(PlatformName.valueOf(platform.toUpperCase().trim()));
            }
            return platformsList;
        }

        /**
         * Get a list of available platform names.
         * 
         * @return list of available platform names
         */
        public static Set<String> availablePlatformNames() {
            return Arrays.stream(PlatformName.values()).map(name -> name.toString().toLowerCase())
                    .collect(Collectors.toSet());
        }
    }
}