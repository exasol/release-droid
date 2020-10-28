package com.exasol.releaserobot.release;

import static com.exasol.releaserobot.Platform.PlatformName.GITHUB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.github.GitHubPlatform;

class ReleaseMakerFactoryTest {
    @Test
    void testCreateReleaseMakerGitHub() {
        final GitHubPlatform platform = Mockito.mock(GitHubPlatform.class);
        when(platform.getPlatformName()).thenReturn(GITHUB);
        assertThat(ReleaseMakerFactory.createReleaseMaker(null, platform, null), instanceOf(GitHubReleaseMaker.class));
    }
}