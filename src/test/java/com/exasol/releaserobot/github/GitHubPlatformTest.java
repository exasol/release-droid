package com.exasol.releaserobot.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.*;
import org.mockito.Mockito;

import com.exasol.releaserobot.GithubGateway;

class GitHubPlatformTest {
    @Test
    void testReleaseThrowsException() throws IOException {
    	 final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
    	 
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHReleaseBuilder releaseBuilderMock = Mockito.mock(GHReleaseBuilder.class);
        
        when(releaseBuilderMock.draft(true)).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.body(anyString())).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.name(anyString())).thenReturn(releaseBuilderMock);
        when(ghRepositoryMock.createRelease(anyString())).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.create()).thenThrow(IOException.class);
        
        final GitHubPlatform platform = new GitHubPlatform(githubGateway);
        final GitHubRelease release = GitHubRelease.builder().version("1.0.0").header("header").releaseLetter("")
                .assets(Map.of("assets", "path")).build();
        assertAll(() -> assertThrows(GitHubException.class, () -> platform.makeNewGitHubRelease(release)),
                () -> verify(releaseBuilderMock, times(1)).draft(true),
                () -> verify(releaseBuilderMock, times(1)).name(anyString()),
                () -> verify(releaseBuilderMock, times(1)).body(anyString()),
                () -> verify(ghRepositoryMock, times(1)).createRelease(anyString()));
    }

    @Test
    void testGetClosedTickets() throws IOException {
    	final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
    	
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHIssue firstIssue = Mockito.mock(GHIssue.class);
        final GHIssue secondIssue = Mockito.mock(GHIssue.class);
        when(firstIssue.getNumber()).thenReturn(24);
        when(firstIssue.isPullRequest()).thenReturn(false);
        when(secondIssue.getNumber()).thenReturn(31);
        when(secondIssue.isPullRequest()).thenReturn(false);
        when(ghRepositoryMock.getIssues(GHIssueState.CLOSED)).thenReturn(List.of(firstIssue, secondIssue));
        final GitHubPlatform platform = new GitHubPlatform(githubGateway);
        assertThat(platform.getClosedTickets(), equalTo(Set.of(24, 31)));
    }

    @Test
    void testGetClosedTicketsThrowsException() throws IOException {
    	final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
    	
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getIssues(GHIssueState.CLOSED)).thenThrow(IOException.class);
        final GitHubPlatform platform = new GitHubPlatform(githubGateway);
        final GitHubException exception = assertThrows(GitHubException.class, platform::getClosedTickets);
        assertThat(exception.getMessage(), containsString("Unable to retrieve a list of closed tickets"));
    }
}