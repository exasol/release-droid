package com.exasol;

import static com.exasol.Platform.PlatformName.GITHUB;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.github.GitHubPlatform;
import com.exasol.repository.*;

class RepositoryHandlerTest {
    @Test
    void testValidate() {
        final GitHubGitRepository repositoryMock = Mockito.mock(GitHubGitRepository.class);
        final GitBranchContent contentMock = Mockito.mock(GitBranchContent.class);
        final GitHubPlatform platform = Mockito.mock(GitHubPlatform.class);
        when(platform.getPlatformName()).thenReturn(GITHUB);
        when(repositoryMock.getRepositoryContent(anyString())).thenReturn(contentMock);
        when(repositoryMock.getDefaultBranchName()).thenReturn("master");
        when(repositoryMock.getLatestTag()).thenReturn(Optional.of("0.5.1"));
        when(contentMock.getVersion()).thenReturn("1.0.0");
        when(contentMock.getChangelogFile()).thenReturn("[1.0.0](changes_1.0.0.md)");
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("1.0.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getHeader()).thenReturn(Optional.of("Test header"));
        when(changesMock.getFileName()).thenReturn("file");
        when(contentMock.getReleaseLetter(contentMock.getVersion())).thenReturn(changesMock);
        final RepositoryHandler projectHandler = new RepositoryHandler(repositoryMock, Set.of(platform));
        assertDoesNotThrow(() -> projectHandler.validate());
    }

    @Test
    void testRelease() {
        final GitHubGitRepository repositoryMock = Mockito.mock(GitHubGitRepository.class);
        final GitBranchContent contentMock = Mockito.mock(GitBranchContent.class);
        final GitHubPlatform platform = Mockito.mock(GitHubPlatform.class);
        when(platform.getPlatformName()).thenReturn(GITHUB);
        when(repositoryMock.getRepositoryContent(anyString())).thenReturn(contentMock);
        when(repositoryMock.getDefaultBranchName()).thenReturn("master");
        when(contentMock.getVersion()).thenReturn("1.0.0");
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(contentMock.getReleaseLetter("1.0.0")).thenReturn(changesMock);
        final RepositoryHandler projectHandler = new RepositoryHandler(repositoryMock, Set.of(platform));
        assertDoesNotThrow(projectHandler::release);
    }
}