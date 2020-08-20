package com.exasol.github;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.*;

class RepositoryHandlerTest {
    @Test
    void testValidate() {
        final GitHubGitRepository repositoryMock = Mockito.mock(GitHubGitRepository.class);
        final GitRepositoryContent contentMock = Mockito.mock(GitRepositoryContent.class);
        when(repositoryMock.getRepositoryContent()).thenReturn(contentMock);
        when(repositoryMock.getLatestReleaseTag()).thenReturn(Optional.of("0.5.1"));
        when(contentMock.getVersion()).thenReturn("1.0.0");
        when(contentMock.getChangelogFile()).thenReturn("[1.0.0](changes_1.0.0.md)");
        final String changes = "# Exasol Test Containers 1.0.0, released \n ## Features"
                + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        when(contentMock.getChangesFile(contentMock.getVersion())).thenReturn(changes);
        final RepositoryHandler projectHandler = new RepositoryHandler(repositoryMock, Set.of(ReleasePlatform.GITHUB));
        assertDoesNotThrow(() -> projectHandler.validate(Optional.empty()));
    }

    @Test
    void testRelease() {
        final GitHubGitRepository repositoryMock = Mockito.mock(GitHubGitRepository.class);
        final GitRepositoryContent contentMock = Mockito.mock(GitRepositoryContent.class);
        when(repositoryMock.getRepositoryContent()).thenReturn(contentMock);
        when(contentMock.getVersion()).thenReturn("1.0.0");
        when(contentMock.getChangesFile("1.0.0")).thenReturn("Release \n letter");
        final RepositoryHandler projectHandler = new RepositoryHandler(repositoryMock, Set.of(ReleasePlatform.GITHUB));
        assertDoesNotThrow(projectHandler::release);
    }
}