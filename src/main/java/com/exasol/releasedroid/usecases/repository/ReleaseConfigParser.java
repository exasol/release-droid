package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.adapter.ListExtractor.extractListOfStrings;

import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * A parser for {@link ReleaseConfig}.
 */
public class ReleaseConfigParser {
    /** key for release platforms in configuration file */
    public static final String RELEASE_PLATFORMS_KEY = "release-platforms";
    /** key for maven artifacts in configuration file */
    public static final String MAVEN_ARTIFACTS_KEY = "maven-artifacts";

    private ReleaseConfigParser() {
        // only static use
    }

    /**
     * Parse release configuration.
     *
     * @param content release configuration as a string
     * @return instance of {@link ReleaseConfig}
     */
    public static ReleaseConfig parse(final String content) {
        final var yaml = new Yaml();
        final Map<String, Object> parsed = yaml.load(content);
        if (parsed == null) {
            return ReleaseConfig.builder().build();
        }
        final Object language = parsed.get("language");
        final ReleaseConfig.Builder builder = ReleaseConfig.builder() //
                .releasePlatforms(extractListOfStrings(parsed, RELEASE_PLATFORMS_KEY)) //
                .language(language == null ? null : language.toString()) //
                .mavenArtifacts(extractListOfStrings(parsed, MAVEN_ARTIFACTS_KEY));
        return builder.build();
    }
}
