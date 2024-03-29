package com.exasol.releasedroid.adapter.communityportal;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.exasol.releasedroid.adapter.ListExtractor;

/**
 * A parser for {@link CommunityConfig}.
 */
public class CommunityConfigParser {
    private static final String PROJECT_NAME_KEY = "community-project-name";
    private static final String PROJECT_DESCRIPTION_KEY = "community-project-description";
    private static final String TAGS_KEY = "community-tags";

    private CommunityConfigParser() {
    }

    /**
     * Parse release config.
     *
     * @param releaseConfigString release config as a string
     * @return release config
     */
    public static CommunityConfig parse(final String releaseConfigString) {
        final var yaml = new Yaml();
        final Map<String, Object> parsedConfig = yaml.load(releaseConfigString);
        if (parsedConfig == null) {
            return CommunityConfig.builder().build();
        }
        final String communityProjectName = getProjectName(parsedConfig);
        final String communityProjectDescription = getProjectDescription(parsedConfig);
        final List<String> communityTags = getTags(parsedConfig);
        return CommunityConfig.builder().communityProjectName(communityProjectName)
                .communityProjectDescription(communityProjectDescription).communityTags(communityTags).build();
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
        return ListExtractor.extractListOfStrings(parsedConfig, TAGS_KEY);
    }
}
