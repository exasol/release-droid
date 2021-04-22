package com.exasol.releasedroid.usecases.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReleaseLetterTest {
    @Test
    void testGetSummary() {
        final String body = "## Summary\n\n" //
                + "We have added a few #new #features.\n\n" //
                + "## Features\n\n" //
                + "* #1: feature 1\n" //
                + "* #2: feature 2";
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("file").body(body).build();
        assertThat(releaseLetter.getSummary().orElseThrow(), equalTo("We have added a few #new #features."));
    }

    @Test
    void testGetSummaryEmpty() {
        final String body = "## Features\n\n" //
                + "* #1: feature 1\n" //
                + "* #2: feature 2";
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("file").body(body).build();
        assertTrue(releaseLetter.getSummary().isEmpty());
    }

    @Test
    void testGetSummaryWithoutNextSection() {
        final String body = "## Summary\n\n" //
                + "We have added a few new features.\n\n";
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("file").body(body).build();
        assertThat(releaseLetter.getSummary().orElseThrow(), equalTo("We have added a few new features."));
    }
}