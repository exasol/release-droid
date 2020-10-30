package com.exasol.releaserobot.release;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.MavenPlatform;
import com.exasol.releaserobot.ReleaseMaker;
import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.report.ReleaseReport;
import com.exasol.releaserobot.repository.GitBranchContent;

class MavenReleaseMakerTest {
    @Test
    void testMakeReleaseShouldSucceed() {
        final MavenPlatform mavenPlatform = mock(MavenPlatform.class);
        final GitBranchContent contentMock = Mockito.mock(GitBranchContent.class);
        when(contentMock.getBranchName()).thenReturn("main");
        final ReleaseMaker releaseMaker = new MavenReleaseMaker(contentMock, mavenPlatform, new ReleaseReport());
        assertAll(() -> assertThat(releaseMaker.makeRelease(), equalTo(true)),
                () -> verify(mavenPlatform, times(1)).makeNewMavenRelease("main"));
    }

    @Test
    void testMakeReleaseShouldFail() throws GitHubException {
        final MavenPlatform mavenPlatform = mock(MavenPlatform.class);
        final ReleaseReport releaseReport = new ReleaseReport();
        final GitBranchContent contentMock = Mockito.mock(GitBranchContent.class);
        when(contentMock.getBranchName()).thenReturn("main");
        final ReleaseMaker releaseMaker = new MavenReleaseMaker(contentMock, mavenPlatform, releaseReport);
        doThrow(GitHubException.class).when(mavenPlatform).makeNewMavenRelease("main");
        assertAll(() -> assertThat(releaseMaker.makeRelease(), equalTo(false)),
                () -> verify(mavenPlatform, times(1)).makeNewMavenRelease("main"),
                () -> assertThat(releaseReport.hasFailures(), equalTo(true)));
    }
}
