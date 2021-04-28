package com.exasol.releasedroid.adapter.communityportal;

import java.util.*;

import org.yaml.snakeyaml.Yaml;

/**
 * A parser for Community portal template.
 */
public class CommunityPortalTemplateParser {
    private static final String PROJECT_NAME_KEY = "community-project-name";
    private static final String PROJECT_DESCRIPTION_KEY = "community-project-description";
    private static final String TAGS_KEY = "community-tags";

    private CommunityPortalTemplateParser() {
    }

    /**
     * Parse community portal template.
     *
     * @param templateAsString community portal template as a string
     * @return community portal template
     */
    public static CommunityPortalTemplate parse(final String templateAsString) {
        final var yaml = new Yaml();
        final Map<String, Object> parsedConfig = yaml.load(templateAsString);
        final String projectName = getProjectName(parsedConfig);
        final String projectDescription = getProjectDescription(parsedConfig);
        final List<String> tags = getTags(parsedConfig);
        return new CommunityPortalTemplate(projectName, projectDescription, tags);
    }

    private static String getProjectName(final Map<String, Object> parsedConfig) {
        return parsedConfig.containsKey(PROJECT_NAME_KEY) ? String.valueOf(parsedConfig.get(PROJECT_NAME_KEY)) : null;
    }

    private static String getProjectDescription(final Map<String, Object> parsedConfig) {
        return parsedConfig.containsKey(PROJECT_DESCRIPTION_KEY)
                ? String.valueOf(parsedConfig.get(PROJECT_DESCRIPTION_KEY))
                : null;
    }

    private static List<String> getTags(final Map<String, Object> parsedConfig) {
        final List<String> tags = new ArrayList<>();
        if (parsedConfig.containsKey(TAGS_KEY) && parsedConfig.get(TAGS_KEY) instanceof Collection) {
            tags.addAll((Collection) parsedConfig.get(TAGS_KEY));
        }
        return tags;
    }
}