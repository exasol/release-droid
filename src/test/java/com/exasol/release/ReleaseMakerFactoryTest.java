package com.exasol.release;

import static com.exasol.ReleasePlatform.GITHUB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.jupiter.api.Test;

class ReleaseMakerFactoryTest {
    @Test
    void testCreateReleaseMakerGitHub() {
        assertThat(ReleaseMakerFactory.createReleaseMaker(null, GITHUB), instanceOf(GitHubReleaseMaker.class));
    }
}