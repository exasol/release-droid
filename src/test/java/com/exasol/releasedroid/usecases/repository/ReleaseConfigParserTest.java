package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.request.PlatformName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class ReleaseConfigParserTest {

    @Test
    void testParseConfig() {
        final ReleaseConfig config = ReleaseConfigParser.parse(getCommunityPortalTemplate());
        assertAll(() -> assertThat(config.getReleasePlatforms(), containsInRelativeOrder(MAVEN, GITHUB, COMMUNITY)));
    }

    @Test
    void testParseEmptyFile() {
        final ReleaseConfig config = ReleaseConfigParser.parse("");
        assertAll(() -> assertThat(config.getReleasePlatforms(), empty()));
    }

    @Test
    void languageOfEmptyConfig_IsEmpty() {
        assertThat(readLanguage(null).isEmpty(), is(true));
    }

    @Test
    void unspecifiedLanguage_IsEmpty() {
        final ReleaseConfig config = ReleaseConfigParser.parse(getCommunityPortalTemplate());
        assertThat(readLanguage(config).isEmpty(), is(true));
    }

    @Test
    void nonEmptyLanguage() {
        final ReleaseConfig config = ReleaseConfigParser.parse(getCommunityPortalTemplate() + "language: Java\n");
        final Optional<String> language = readLanguage(config);
        assertThat(language.isPresent(), is(true));
        assertThat(language.get(), equalTo("Java"));
    }

    private Optional<String> readLanguage(final ReleaseConfig config) {
        return Optional.ofNullable(config) //
                .map(ReleaseConfig::getLanguage) //
                .orElse(Optional.empty());
    }

    private String getCommunityPortalTemplate() {
        return "release-platforms:\n" //
                + "- GitHub\n" //
                + "- Community\n" //
                + "- Maven\n";
    }
}