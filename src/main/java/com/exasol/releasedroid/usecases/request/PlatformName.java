package com.exasol.releasedroid.usecases.request;

import java.util.*;
import java.util.stream.Collectors;

import com.exasol.errorreporting.ExaError;

/**
 * This class contains supported release platforms.
 */
public enum PlatformName {
    GITHUB, MAVEN, COMMUNITY;

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
        platformsList.sort(Comparator.comparingInt(List.of(MAVEN, GITHUB, COMMUNITY)::indexOf));
    }

    private static PlatformName getPlatformName(final String platform) {
        try {
            return PlatformName.valueOf(platform.toUpperCase().trim());
        } catch (final IllegalArgumentException exception) {
            final Set<String> availablePlatforms = Arrays.stream(PlatformName.values())
                    .map(name -> name.toString().toLowerCase()).collect(Collectors.toSet());
            throw new IllegalArgumentException(ExaError.messageBuilder("E-RD-12") //
                    .message("Cannot parse a platform {{platform}}.").parameter("platform", platform)
                    .mitigation("Please, use one of the following platforms: {{availablePlatforms|uq}}.",
                            String.join(",", availablePlatforms))
                    .toString(), exception);
        }
    }
}