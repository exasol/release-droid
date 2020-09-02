package com.exasol.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.mockito.Mockito;

import com.exasol.github.GitHubException;

class GitHubGitRepositoryTest {
    @Test
    void testGetLatestReleaseVersion() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHRelease releaseMock = Mockito.mock(GHRelease.class);
        when(releaseMock.getTagName()).thenReturn("1.0.0");
        when(ghRepositoryMock.getLatestRelease()).thenReturn(releaseMock);
        final GitRepository repository = new GitHubGitRepository(ghRepositoryMock);
        final Optional<String> latestReleaseTag = repository.getLatestTag();
        assertThat(latestReleaseTag.isPresent(), equalTo(true));
        assertThat(latestReleaseTag.get(), equalTo("1.0.0"));
    }

    @Test
    void testGetLatestReleaseVersionEmpty() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getLatestRelease()).thenReturn(null);
        final GitRepository repository = new GitHubGitRepository(ghRepositoryMock);
        final Optional<String> latestReleaseTag = repository.getLatestTag();
        assertThat(latestReleaseTag.isPresent(), equalTo(false));
    }

    @Test
    void testGetLatestReleaseVersionThrowsException() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getLatestRelease()).thenThrow(IOException.class);
        final GitRepository repository = new GitHubGitRepository(ghRepositoryMock);
        assertThrows(GitHubException.class, repository::getLatestTag);
    }

    @Test
    void testGetDefaultBranchName() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getDefaultBranch()).thenReturn("dev");
        final GitRepository repository = new GitHubGitRepository(ghRepositoryMock);
        assertThat(repository.getDefaultBranchName(), equalTo("dev"));
    }

    @Test
    void testGetRepositoryContent() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GitRepository repository = new GitHubGitRepository(ghRepositoryMock);
        assertThat(repository.getRepositoryContent("dev"), instanceOf(JavaMavenGitRepositoryContent.class));
    }
}