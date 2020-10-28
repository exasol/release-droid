package com.exasol.releaserobot.release;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.exasol.releaserobot.MavenPlatform;
import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.report.ReleaseReport;

class MavenReleaseMakerTest {
    @Test
    void testMakeRelease() {
        final MavenPlatform mavenPlatform = mock(MavenPlatform.class);
        final ReleaseMaker releaseMaker = new MavenReleaseMaker(mavenPlatform, new ReleaseReport());
        assertAll(() -> assertThat(releaseMaker.makeRelease(), equalTo(true)),
                () -> verify(mavenPlatform, times(1)).makeNewMavenRelease());
    }

    @Test
    void testMakeReleaseFailed() {
        final MavenPlatform mavenPlatform = mock(MavenPlatform.class);
        final ReleaseReport releaseReport = new ReleaseReport();
        final ReleaseMaker releaseMaker = new MavenReleaseMaker(mavenPlatform, releaseReport);
        doThrow(GitHubException.class).when(mavenPlatform).makeNewMavenRelease();
        assertAll(() -> assertThat(releaseMaker.makeRelease(), equalTo(false)),
                () -> verify(mavenPlatform, times(1)).makeNewMavenRelease(),
                () -> assertThat(releaseReport.hasFailures(), equalTo(true)));
    }
}