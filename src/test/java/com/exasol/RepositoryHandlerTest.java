package com.exasol;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.git.GitRepositoryContent;
import com.exasol.git.ReleaseChangesLetter;
import com.exasol.github.GitHubGitRepository;

class RepositoryHandlerTest {
    @Test
    void testValidate() {
        final GitHubGitRepository repositoryMock = Mockito.mock(GitHubGitRepository.class);
        final GitRepositoryContent contentMock = Mockito.mock(GitRepositoryContent.class);
        when(repositoryMock.getRepositoryContent(anyString())).thenReturn(contentMock);
        when(repositoryMock.getDefaultBranchName()).thenReturn("master");
        when(repositoryMock.getLatestTag()).thenReturn(Optional.of("0.5.1"));
        when(contentMock.getVersion()).thenReturn("1.0.0");
        when(contentMock.getChangelogFile()).thenReturn("[1.0.0](changes_1.0.0.md)");
        final ReleaseChangesLetter changesMock = Mockito.mock(ReleaseChangesLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("1.0.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getHeader()).thenReturn(Optional.of("Test header"));
        when(changesMock.getFileName()).thenReturn("file");
        when(contentMock.getReleaseChangesLetter(contentMock.getVersion())).thenReturn(changesMock);
        final RepositoryHandler projectHandler = new RepositoryHandler(repositoryMock, Set.of(ReleasePlatform.GITHUB));
        assertDoesNotThrow(() -> projectHandler.validate());
    }

    @Test
    void testRelease() {
        final GitHubGitRepository repositoryMock = Mockito.mock(GitHubGitRepository.class);
        final GitRepositoryContent contentMock = Mockito.mock(GitRepositoryContent.class);
        when(repositoryMock.getRepositoryContent(anyString())).thenReturn(contentMock);
        when(repositoryMock.getDefaultBranchName()).thenReturn("master");
        when(contentMock.getVersion()).thenReturn("1.0.0");
        final ReleaseChangesLetter changesMock = Mockito.mock(ReleaseChangesLetter.class);
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(contentMock.getReleaseChangesLetter("1.0.0")).thenReturn(changesMock);
        final RepositoryHandler projectHandler = new RepositoryHandler(repositoryMock, Set.of(ReleasePlatform.GITHUB));
        assertDoesNotThrow(projectHandler::release);
    }
}