package com.exasol.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.*;
import org.mockito.Mockito;

import com.exasol.git.GitRepository;

class GitHubGitRepositoryTest {
    @Test
    void testGetLatestReleaseVersion() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHRelease releaseMock = Mockito.mock(GHRelease.class);
        when(releaseMock.getTagName()).thenReturn("1.0.0");
        when(ghRepositoryMock.getLatestRelease()).thenReturn(releaseMock);
        final GitRepository repository = new GitHubGitRepository(ghRepositoryMock, new GitHubUser("", ""));
        final Optional<String> latestReleaseTag = repository.getLatestTag();
        assertThat(latestReleaseTag.isPresent(), equalTo(true));
        assertThat(latestReleaseTag.get(), equalTo("1.0.0"));
    }

    @Test
    void testGetLatestReleaseVersionEmpty() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getLatestRelease()).thenReturn(null);
        final GitRepository repository = new GitHubGitRepository(ghRepositoryMock, new GitHubUser("", ""));
        final Optional<String> latestReleaseTag = repository.getLatestTag();
        assertThat(latestReleaseTag.isPresent(), equalTo(false));
    }

    @Test
    void testGetLatestReleaseVersionThrowsException() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getLatestRelease()).thenThrow(IOException.class);
        final GitRepository repository = new GitHubGitRepository(ghRepositoryMock, new GitHubUser("", ""));
        assertThrows(GitHubException.class, repository::getLatestTag);
    }

    @Test
    void testReleaseThrowsException() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHReleaseBuilder releaseBuilderMock = Mockito.mock(GHReleaseBuilder.class);
        when(releaseBuilderMock.draft(true)).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.body(anyString())).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.name(anyString())).thenReturn(releaseBuilderMock);
        when(ghRepositoryMock.createRelease(anyString())).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.create()).thenThrow(IOException.class);
        final GitRepository repository = new GitHubGitRepository(ghRepositoryMock, new GitHubUser("", ""));
        assertAll(() -> assertThrows(GitHubException.class, () -> repository.release("", "")),
                () -> verify(releaseBuilderMock, times(1)).draft(true),
                () -> verify(releaseBuilderMock, times(1)).name(anyString()),
                () -> verify(releaseBuilderMock, times(1)).body(anyString()),
                () -> verify(ghRepositoryMock, times(1)).createRelease(anyString()));
    }
}