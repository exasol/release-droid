package com.exasol.releaserobot.release;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.ReleaseMaker;
import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.github.GitHubPlatform;
import com.exasol.releaserobot.report.ReleaseReport;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.ReleaseLetter;

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
        when(contentMock.getBranchName()).thenReturn("main");
        when(contentMock.getDeliverables()).thenReturn(Map.of("name", "path"));
        when(contentMock.getReleaseLetter(version)).thenReturn(changesMock);
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(contentMock, gitHubPlatform, new ReleaseReport());
        assertAll(() -> assertThat(releaseMaker.makeRelease(), equalTo(true)),
                () -> verify(contentMock, times(1)).getReleaseLetter(version),
                () -> verify(gitHubPlatform, times(1)).makeNewGitHubRelease(any()));
    }

    @Test
    void testMakeReleaseFailedRelease() throws GitHubException {
        final String version = "1.0.0";
        final GitBranchContent contentMock = mock(GitBranchContent.class);
        final ReleaseLetter changesMock = mock(ReleaseLetter.class);
        final GitHubPlatform gitHubPlatform = mock(GitHubPlatform.class);
        doThrow(GitHubException.class).when(gitHubPlatform).makeNewGitHubRelease(any());
        when(changesMock.getBody()).thenReturn(Optional.empty());
        when(contentMock.getVersion()).thenReturn(version);
        when(contentMock.getBranchName()).thenReturn("main");
        when(contentMock.getDeliverables()).thenReturn(Map.of("name", "path"));
        when(contentMock.getReleaseLetter(version)).thenReturn(changesMock);
        final ReleaseReport releaseReport = new ReleaseReport();
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(contentMock, gitHubPlatform, releaseReport);
        assertAll(() -> assertThat(releaseMaker.makeRelease(), equalTo(false)),
                () -> verify(gitHubPlatform, times(1)).makeNewGitHubRelease(any()),
                () -> assertThat(releaseReport.hasFailures(), equalTo(true)));
    }
}