package com.exasol.releasedroid.usecases;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains supported release platforms.
 */
public enum PlatformName {
    GITHUB, MAVEN;

    /**
     * Create a list of {@link PlatformName}s from Strings.
     *
     * @param platforms one or more platform names as Strings
     * @return list of members of {@link PlatformName} enum class
     */
    public static List<PlatformName> toList(final String... platforms) {
        final List<PlatformName> platformsList = new ArrayList<>();
        for (final String platform : platforms) {
            platformsList.add(getPlatformName(platform));
        }
        sortPlatforms(platformsList);
        return platformsList;
    }

    /**
     * Sort the platforms in pre-defined priority order. 1. MAVEN 2. GITHUB
     */
    private static void sortPlatforms(final List<PlatformName> platformsList) {
        platformsList.sort(Comparator.comparingInt(List.of(MAVEN, GITHUB)::indexOf));
    }

    private static PlatformName getPlatformName(final String platform) {
        try {
            return PlatformName.valueOf(platform.toUpperCase().trim());
        } catch (final IllegalArgumentException exception) {
            final Set<String> availablePlatforms = Arrays.stream(PlatformName.values())
                    .map(name -> name.toString().toLowerCase()).collect(Collectors.toSet());
            throw new IllegalArgumentException(MessageFormat.format(
                    "E-RR-PL-1: Cannot parse a platform '{}'. Please, use one of the following platforms: {}", platform,
                    String.join(",", availablePlatforms)), exception);
        }
    }
}