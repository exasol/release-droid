package com.exasol.releasedroid.adapter.jira;

import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * A parser for Jira configs.
 */
public class JiraConfigParser {
    private static final String PROJECT_NAME_KEY = "human-readable-project-name";

    private JiraConfigParser() {
    }

    /**
     * Parse release config.
     *
     * @param releaseConfigString release config as a string
     * @return project name
     */
    public static String parse(final String releaseConfigString) {
        final var yaml = new Yaml();
        final Map<String, Object> parsedConfig = yaml.load(releaseConfigString);
        if (parsedConfig == null) {
            return null;
        }
        return getProjectName(parsedConfig);
    }

    private static String getProjectName(final Map<String, Object> parsedConfig) {
        return extractString(parsedConfig, PROJECT_NAME_KEY);
    }

    private static String extractString(final Map<String, Object> parsedConfig, final String key) {
        return parsedConfig.containsKey(key) ? String.valueOf(parsedConfig.get(key)) : null;
    }
}