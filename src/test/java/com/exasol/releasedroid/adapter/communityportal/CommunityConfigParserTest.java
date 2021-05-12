package com.exasol.releasedroid.adapter.communityportal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class CommunityConfigParserTest {
    @Test
    void testParseConfig() {
        final CommunityConfig config = getConfig(getCommunityPortalTemplate());
        assertAll(() -> assertThat(config.getCommunityProjectName(), equalTo("Virtual Schema for ElasticSearch")),
                () -> assertThat(config.getCommunityProjectDescription(), equalTo("Here is a project description.")),
                () -> assertThat(config.getCommunityTags(),
                        containsInAnyOrder("Release Droid", "Java Tools", "Open Source", "GitHub")),
                () -> assertThat(config.hasCommunityProjectDescription(), equalTo(true)), //
                () -> assertThat(config.hasCommunityProjectName(), equalTo(true)), //
                () -> assertThat(config.hasCommunityTags(), equalTo(true)) //
        );
    }

    private CommunityConfig getConfig(final String config) {
        return CommunityConfigParser.parse(config);
    }

    @Test
    void testParseEmptyFile() {
        final CommunityConfig config = getConfig("");
        assertAll(() -> assertThat(config.getCommunityProjectName(), equalTo(null)),
                () -> assertThat(config.getCommunityProjectDescription(), equalTo(null)),
                () -> assertThat(config.getCommunityTags(), equalTo(null)),
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
        ;
    }
}