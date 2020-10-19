package com.exasol.release.robot.release;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.release.robot.Platform.PlatformName;
import com.exasol.release.robot.github.GitHubPlatform;

class ReleaseMakerFactoryTest {
    @Test
    void testCreateReleaseMakerGitHub() {
        final GitHubPlatform platform = Mockito.mock(GitHubPlatform.class);
        when(platform.getPlatformName()).thenReturn(PlatformName.GITHUB);
        assertThat(ReleaseMakerFactory.createReleaseMaker(null, platform, null), instanceOf(GitHubReleaseMaker.class));
    }

    @Test
    void testCreateReleaseMakerUnsupported() {
        final GitHubPlatform platform = Mockito.mock(GitHubPlatform.class);
        when(platform.getPlatformName()).thenReturn(PlatformName.MAVEN);
        assertThrows(UnsupportedOperationException.class,
                () -> ReleaseMakerFactory.createReleaseMaker(null, platform, null));
    }
}