package com.exasol.release.robot.release;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.release.robot.github.GitHubException;
import com.exasol.release.robot.github.GitHubPlatform;
import com.exasol.release.robot.report.ReleaseReport;
import com.exasol.release.robot.repository.GitBranchContent;
import com.exasol.release.robot.repository.ReleaseLetter;

class GitHubReleaseMakerTest {
    @Test
    // The changes files must be checked on the validation stage. Here we throw no exception even if it's not valid
    // [utest->dsn~create-new-github-release~1]
    // [utest->dsn~retrieve-github-release-header-from-release-letter~1]
    // [utest->dsn~retrieve-github-release-body-from-release-letter~1]
    void testMakeRelease() {
        final String version = "1.0.0";
        final GitBranchContent contentMock = Mockito.mock(GitBranchContent.class);
        final ReleaseLetter changesMock = mock(ReleaseLetter.class);
        final GitHubPlatform gitHubPlatform = mock(GitHubPlatform.class);
        when(changesMock.getBody()).thenReturn(Optional.empty());
        when(contentMock.getVersion()).thenReturn(version);
        when(contentMock.getDeliverables()).thenReturn(Map.of("name", "path"));
        when(contentMock.getReleaseLetter(version)).thenReturn(changesMock);
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(contentMock, gitHubPlatform, new ReleaseReport());
        assertAll(() -> assertThat(releaseMaker.makeRelease(), equalTo(true)),
                () -> verify(contentMock, times(1)).getReleaseLetter(version),
                () -> verify(gitHubPlatform, times(1)).release(any()));
    }

    @Test
    void testMakeReleaseFailedRelease() {
        final String version = "1.0.0";
        final GitBranchContent contentMock = mock(GitBranchContent.class);
        final ReleaseLetter changesMock = mock(ReleaseLetter.class);
        final GitHubPlatform gitHubPlatform = mock(GitHubPlatform.class);
        doThrow(GitHubException.class).when(gitHubPlatform).release(any());
        when(changesMock.getBody()).thenReturn(Optional.empty());
        when(contentMock.getVersion()).thenReturn(version);
        when(contentMock.getDeliverables()).thenReturn(Map.of("name", "path"));
        when(contentMock.getReleaseLetter(version)).thenReturn(changesMock);
        final ReleaseReport releaseReport = new ReleaseReport();
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(contentMock, gitHubPlatform, releaseReport);
        assertAll(() -> assertThat(releaseMaker.makeRelease(), equalTo(false)),
                () -> verify(gitHubPlatform, times(1)).release(any()),
                () -> assertThat(releaseReport.hasFailures(), equalTo(true)));
    }
}