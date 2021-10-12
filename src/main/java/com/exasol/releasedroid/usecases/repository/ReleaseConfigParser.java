package com.exasol.releasedroid.usecases.repository;

import java.util.*;

import org.yaml.snakeyaml.Yaml;

/**
 * A parser for {@link ReleaseConfig}.
 */
public class ReleaseConfigParser {
    private static final String RELEASE_PLATFORMS_KEY = "release-platforms";

    private ReleaseConfigParser() {
    }

    /**
     * Parse release config.
     *
     * @param releaseConfigString release config as a string
     * @return release config
     */
    public static ReleaseConfig parse(final String releaseConfigString) {
        final var yaml = new Yaml();
        final Map<String, Object> parsedConfig = yaml.load(releaseConfigString);
        if (parsedConfig == null) {
            return ReleaseConfig.builder().build();
        }
        final List<String> releasePlatforms = getReleasePlatforms(parsedConfig);
        return ReleaseConfig.builder().releasePlatforms(releasePlatforms).build();
    }

    private static List<String> getReleasePlatforms(final Map<String, Object> parsedConfig) {
        return extractListOfStrings(parsedConfig, RELEASE_PLATFORMS_KEY);
    }

    private static List<String> extractListOfStrings(final Map<String, Object> parsedConfig, final String key) {
        final List<String> tags = new ArrayList<>();
        if (parsedConfig.containsKey(key) && (parsedConfig.get(key) instanceof Collection)) {
            @SuppressWarnings("unchecked")
            final Collection<String> configTags = (Collection<String>) parsedConfig.get(key);
            tags.addAll(configTags);
        }
        return tags;
    }
}
