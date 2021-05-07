package com.exasol.releasedroid.usecases.repository;

import java.util.*;

import org.yaml.snakeyaml.Yaml;

/**
 * A parser for {@link ReleaseConfig}.
 */
public class ReleaseConfigParser {
    private static final String RELEASE_PLATFORMS_KEY = "release-platforms";
    private static final String PROJECT_NAME_KEY = "community-project-name";
    private static final String PROJECT_DESCRIPTION_KEY = "community-project-description";
    private static final String TAGS_KEY = "community-tags";

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
        final String communityProjectName = getProjectName(parsedConfig);
        final String communityProjectDescription = getProjectDescription(parsedConfig);
        final List<String> communityTags = getTags(parsedConfig);
        final List<String> releasePlatforms = getReleasePlatforms(parsedConfig);
        return ReleaseConfig.builder().communityProjectName(communityProjectName)
                .communityProjectDescription(communityProjectDescription).communityTags(communityTags)
                .releasePlatforms(releasePlatforms).build();
    }

    private static String getProjectName(final Map<String, Object> parsedConfig) {
        return extractString(parsedConfig, PROJECT_NAME_KEY);
    }

    private static String getProjectDescription(final Map<String, Object> parsedConfig) {
        return extractString(parsedConfig, PROJECT_DESCRIPTION_KEY);
    }

    private static String extractString(final Map<String, Object> parsedConfig, final String key) {
        return parsedConfig.containsKey(key) ? String.valueOf(parsedConfig.get(key)) : null;
    }

    private static List<String> getTags(final Map<String, Object> parsedConfig) {
        return extractListOfStrings(parsedConfig, TAGS_KEY);
    }

    private static List<String> getReleasePlatforms(final Map<String, Object> parsedConfig) {
        return extractListOfStrings(parsedConfig, RELEASE_PLATFORMS_KEY);
    }

    private static List<String> extractListOfStrings(final Map<String, Object> parsedConfig, final String key) {
        final List<String> tags = new ArrayList<>();
        if (parsedConfig.containsKey(key) && parsedConfig.get(key) instanceof Collection) {
            tags.addAll((Collection) parsedConfig.get(key));
        }
        return tags;
    }
}
