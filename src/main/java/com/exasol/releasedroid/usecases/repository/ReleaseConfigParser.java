package com.exasol.releasedroid.usecases.repository;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.exasol.releasedroid.adapter.ListExtractor;

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

        final Object language = parsedConfig.get("language");
        return ReleaseConfig.builder().releasePlatforms(releasePlatforms) //
                .language(language == null ? null : language.toString()) //
                .build();
    }

    private static List<String> getReleasePlatforms(final Map<String, Object> parsedConfig) {
        return ListExtractor.extractListOfStrings(parsedConfig, RELEASE_PLATFORMS_KEY);
    }
}
