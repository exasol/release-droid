package com.exasol.github;

import static com.exasol.Platform.PlatformName.GITHUB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.*;
import org.mockito.Mockito;

class GitHubPlatformTest {
    @Test
    void testReleaseThrowsException() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHReleaseBuilder releaseBuilderMock = Mockito.mock(GHReleaseBuilder.class);
        when(releaseBuilderMock.draft(true)).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.body(anyString())).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.name(anyString())).thenReturn(releaseBuilderMock);
        when(ghRepositoryMock.createRelease(anyString())).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.create()).thenThrow(IOException.class);
        final GitHubPlatform platform = new GitHubPlatform(GITHUB, ghRepositoryMock, new GitHubUser("", ""));
        assertAll(() -> assertThrows(GitHubException.class, () -> platform.release("", "", "")),
                () -> verify(releaseBuilderMock, times(1)).draft(true),
                () -> verify(releaseBuilderMock, times(1)).name(anyString()),
                () -> verify(releaseBuilderMock, times(1)).body(anyString()),
                () -> verify(ghRepositoryMock, times(1)).createRelease(anyString()));
    }

    @Test
    void testGetClosedTickets() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHIssue firstIssue = Mockito.mock(GHIssue.class);
        final GHIssue secondIssue = Mockito.mock(GHIssue.class);
        when(firstIssue.getNumber()).thenReturn(24);
        when(secondIssue.getNumber()).thenReturn(31);
        when(ghRepositoryMock.getIssues(GHIssueState.CLOSED)).thenReturn(List.of(firstIssue, secondIssue));
        final GitHubPlatform platform = new GitHubPlatform(GITHUB, ghRepositoryMock, new GitHubUser("", ""));
        assertThat(platform.getClosedTickets(), equalTo(Set.of(24, 31)));
    }

    @Test
    void testGetClosedTicketsThrowsException() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getIssues(GHIssueState.CLOSED)).thenThrow(IOException.class);
        final GitHubPlatform platform = new GitHubPlatform(GITHUB, ghRepositoryMock, new GitHubUser("", ""));
        final GitHubException exception = assertThrows(GitHubException.class, platform::getClosedTickets);
        assertThat(exception.getMessage(), containsString("Unable to retrieve a list of closed tickets"));
    }
}