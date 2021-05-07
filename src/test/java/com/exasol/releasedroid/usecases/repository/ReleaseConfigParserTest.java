package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.request.PlatformName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class ReleaseConfigParserTest {
    @Test
    void testParseConfig() {
        final ReleaseConfig config = ReleaseConfigParser.parse(getCommunityPortalTemplate());
        assertAll(() -> assertThat(config.getReleasePlatforms(), containsInRelativeOrder(MAVEN, GITHUB, COMMUNITY)),
                () -> assertThat(config.getCommunityProjectName(), equalTo("Virtual Schema for ElasticSearch")),
                () -> assertThat(config.getCommunityProjectDescription(), equalTo("Here is a project description.")),
                () -> assertThat(config.getCommunityTags(),
                        containsInAnyOrder("Release Droid", "Java Tools", "Open Source", "GitHub")),
                () -> assertThat(config.hasReleasePlatforms(), equalTo(true)), //
                () -> assertThat(config.hasCommunityProjectDescription(), equalTo(true)), //
                () -> assertThat(config.hasCommunityProjectName(), equalTo(true)), //
                () -> assertThat(config.hasCommunityTags(), equalTo(true)) //
        );
    }

    @Test
    void testParseEmptyFile() {
        final ReleaseConfig config = ReleaseConfigParser.parse("");
        assertAll(() -> assertThat(config.getReleasePlatforms(), equalTo(null)),
                () -> assertThat(config.getCommunityProjectName(), equalTo(null)),
                () -> assertThat(config.getCommunityProjectDescription(), equalTo(null)),
                () -> assertThat(config.getCommunityTags(), equalTo(null)),
                () -> assertThat(config.hasReleasePlatforms(), equalTo(false)), //
                () -> assertThat(config.hasCommunityProjectDescription(), equalTo(false)), //
                () -> assertThat(config.hasCommunityProjectName(), equalTo(false)), //
                () -> assertThat(config.hasCommunityTags(), equalTo(false)) //
        );
    }

    private String getCommunityPortalTemplate() {
        return "community-tags:\n" //
                + "- Release Droid\n" //
                + "- Java Tools\n" //
                + "- Open Source\n" //
                + "- GitHub\n" //
                + "community-project-name: Virtual Schema for ElasticSearch\n" //
                + "community-project-description: Here is a project description.\n" //
                + "release-platforms:\n" //
                + "- GitHub\n" //
                + "- Community\n" //
                + "- Maven\n" //
        ;
    }
}