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

class GitHubRepositoryTest {
    @Test
    void testGetLatestReleaseVersion() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHRelease releaseMock = Mockito.mock(GHRelease.class);
        when(releaseMock.getTagName()).thenReturn("1.0.0");
        when(ghRepositoryMock.getLatestRelease()).thenReturn(releaseMock);
        final GitHubRepository repository = new DummyGitHubRepository(ghRepositoryMock);
        final Optional<String> latestReleaseTag = repository.getLatestReleaseVersion();
        assertThat(latestReleaseTag.isPresent(), equalTo(true));
        assertThat(latestReleaseTag.get(), equalTo("1.0.0"));
    }

    @Test
    void testGetLatestReleaseVersionEmpty() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getLatestRelease()).thenReturn(null);
        final GitHubRepository repository = new DummyGitHubRepository(ghRepositoryMock);
        final Optional<String> latestReleaseTag = repository.getLatestReleaseVersion();
        assertThat(latestReleaseTag.isPresent(), equalTo(false));
    }

    @Test
    void testGetLatestReleaseVersionThrowsException() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getLatestRelease()).thenThrow(IOException.class);
        final GitHubRepository repository = new DummyGitHubRepository(ghRepositoryMock);
        assertThrows(GitHubException.class, repository::getLatestReleaseVersion);
    }

    @Test
    void testGetChangelogFile() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final String textContent = "Text content";
        when(contentMock.getContent()).thenReturn(textContent);
        when(ghRepositoryMock.getFileContent(anyString())).thenReturn(contentMock);
        final GitHubRepository repository = new DummyGitHubRepository(ghRepositoryMock);
        assertThat(repository.getChangelogFile(), equalTo(textContent));
    }

    @Test
    void testGetChangelogFileThrowsException() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        when(ghRepositoryMock.getFileContent(anyString())).thenThrow(IOException.class);
        final GitHubRepository repository = new DummyGitHubRepository(ghRepositoryMock);
        assertThrows(GitHubException.class, repository::getChangelogFile);
    }

    @Test
    void testGetChangesFileWithCaching() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final String textContent = "Text content";
        when(contentMock.getContent()).thenReturn(textContent);
        when(ghRepositoryMock.getFileContent(anyString())).thenReturn(contentMock);
        final GitHubRepository repository = new DummyGitHubRepository(ghRepositoryMock);
        assertAll(() -> assertThat(repository.getChangesFile(), equalTo(textContent)),
                () -> assertThat(repository.getChangesFile(), equalTo(textContent)),
                () -> verify(ghRepositoryMock, times(1)).getFileContent(anyString()));
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
        final GitHubRepository repository = new DummyGitHubRepository(ghRepositoryMock);
        assertAll(() -> assertThrows(GitHubException.class, () -> repository.release("", "", "")),
                () -> verify(releaseBuilderMock, times(1)).draft(true),
                () -> verify(releaseBuilderMock, times(1)).name(anyString()),
                () -> verify(releaseBuilderMock, times(1)).body(anyString()),
                () -> verify(ghRepositoryMock, times(1)).createRelease(anyString()));

    }

    static class DummyGitHubRepository extends AbstractGitHubRepository {
        protected DummyGitHubRepository(final GHRepository repository) {
            super(repository, "");
        }

        @Override
        public String getVersion() {
            return null;
        }
    }
}