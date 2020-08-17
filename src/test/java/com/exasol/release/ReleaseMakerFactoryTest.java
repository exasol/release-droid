package com.exasol.release;

import static com.exasol.ReleasePlatform.GITHUB;
import static com.exasol.ReleasePlatform.MAVEN_CENTRAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ReleaseMakerFactoryTest {
    @Test
    void testCreateReleaseMakerGitHub() {
        assertThat(ReleaseMakerFactory.createReleaseMaker(null, GITHUB), instanceOf(GitHubReleaseMaker.class));
    }

    @Test
    void testCreateReleaseMakerInvalidPlatform() {
        assertThrows(IllegalArgumentException.class, () -> ReleaseMakerFactory.createReleaseMaker(null, MAVEN_CENTRAL));
    }
}