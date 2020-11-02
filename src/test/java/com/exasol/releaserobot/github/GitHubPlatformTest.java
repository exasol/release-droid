package com.exasol.releaserobot.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.ReleaseMaker;

class GitHubPlatformTest {
    @Test
    void testReleaseThrowsException() throws GitHubException {
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        final ReleaseMaker releaseMaker = Mockito.mock(ReleaseMaker.class);
        doThrow(GitHubException.class).when(releaseMaker).makeRelease();
        final GitHubPlatform platform = new GitHubPlatform(releaseMaker, githubGateway);
        assertAll(() -> assertThrows(GitHubException.class, () -> platform.release(null)),
                () -> verify(releaseMaker, times(1)).makeRelease());
    }

    @Test
    void testGetClosedTickets() throws GitHubException {
        final ReleaseMaker releaseMaker = Mockito.mock(ReleaseMaker.class);
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        when(githubGateway.getClosedTickets()).thenReturn(Set.of(24, 31));
        final GitHubPlatform platform = new GitHubPlatform(releaseMaker, githubGateway);
        assertThat(platform.getClosedTickets(), equalTo(Set.of(24, 31)));
    }

    @Test
    void testGetClosedTicketsThrowsException() throws GitHubException {
        final ReleaseMaker releaseMaker = Mockito.mock(ReleaseMaker.class);
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        when(githubGateway.getClosedTickets()).thenThrow(GitHubException.class);
        final GitHubPlatform platform = new GitHubPlatform(releaseMaker, githubGateway);
        assertThrows(IllegalStateException.class, platform::getClosedTickets);
    }
}