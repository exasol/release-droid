package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.request.PlatformName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class ReleaseConfigParserTest {
    @Test
    void testParseConfig() {
        final ReleaseConfig config = ReleaseConfigParser.parse(getCommunityPortalTemplate());
        assertAll(() -> assertThat(config.getReleasePlatforms(), containsInRelativeOrder(MAVEN, GITHUB, COMMUNITY)),
                () -> assertThat(config.hasReleasePlatforms(), equalTo(true)) //
        );
    }

    @Test
    void testParseEmptyFile() {
        final ReleaseConfig config = ReleaseConfigParser.parse("");
        assertAll(() -> assertThat(config.getReleasePlatforms(), equalTo(null)),
                () -> assertThat(config.hasReleasePlatforms(), equalTo(false)) //
        );
    }

    private String getCommunityPortalTemplate() {
        return "release-platforms:\n" //
                + "- GitHub\n" //
                + "- Community\n" //
                + "- Maven\n" //
        ;
    }
}