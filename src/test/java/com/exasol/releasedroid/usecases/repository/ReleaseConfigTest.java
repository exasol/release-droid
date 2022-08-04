package com.exasol.releasedroid.usecases.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.junit.jupiter.api.Test;

class ReleaseConfigTest {

    @Test
    void empty_IsEqual() {
        assertThat(ReleaseConfig.builder().build(), equalTo(ReleaseConfig.builder().build()));
    }

    @Test
    void identicalLanguage_IsEqual() {
        assertThat(ReleaseConfig.builder().language("lang").build(),
                equalTo(ReleaseConfig.builder().language("lang").build()));
    }

    @Test
    void identicalPlatforms_IsEqual() {
        final List<String> platforms = List.of("Jira", "Maven");
        assertThat(ReleaseConfig.builder().releasePlatforms(platforms).build(),
                equalTo(ReleaseConfig.builder().releasePlatforms(platforms).build()));
    }

    @Test
    void differentPlatforms_IsDifferent() {
        final List<String> platforms = List.of("Jira", "Maven");
        assertThat(ReleaseConfig.builder().releasePlatforms(platforms).build(),
                not(equalTo(ReleaseConfig.builder().releasePlatforms(List.of("Github", "Maven")).build())));
        assertThat(ReleaseConfig.builder().releasePlatforms(platforms).build(),
                not(equalTo(ReleaseConfig.builder().build())));
    }

    @Test
    void differentLanguages_IsDifferent() {
        assertThat(ReleaseConfig.builder().language("Java").build(),
                not(equalTo(ReleaseConfig.builder().language("Scala").build())));
        assertThat(ReleaseConfig.builder().language("Java").build(), //
                not(equalTo(ReleaseConfig.builder().build())));
    }

}
