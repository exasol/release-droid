package com.exasol.releaserobot.maven;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.github.GithubGateway;
import com.exasol.releaserobot.repository.Branch;
import com.exasol.releaserobot.usecases.release.ReleaseMaker;

class MavenReleaseMakerTest {
    @Test
    void testMakeRelease() {
        final GithubGateway githubGateway = mock(GithubGateway.class);
        final Branch branchMock = Mockito.mock(Branch.class);
        when(branchMock.getBranchName()).thenReturn("main");
        final ReleaseMaker releaseMaker = new MavenReleaseMaker(githubGateway);
        assertAll(() -> assertDoesNotThrow(() -> releaseMaker.makeRelease(branchMock)),
                () -> verify(githubGateway, times(1)).sendGitHubRequest(any(), anyString()));
    }

    @Test
    void testMakeReleaseFails() throws GitHubException {
        final GithubGateway githubGateway = mock(GithubGateway.class);
        final Branch branchMock = Mockito.mock(Branch.class);
        when(branchMock.getBranchName()).thenReturn("main");
        final ReleaseMaker releaseMaker = new MavenReleaseMaker(githubGateway);
        doThrow(GitHubException.class).when(githubGateway).sendGitHubRequest(any(), anyString());
        assertAll(() -> assertThrows(GitHubException.class, () -> releaseMaker.makeRelease(branchMock)),
                () -> verify(githubGateway, times(1)).sendGitHubRequest(any(), anyString()));
    }
}