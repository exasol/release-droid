package com.exasol.github;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.ReleasePlatform;
import com.exasol.RepositoryHandler;
import com.exasol.git.GitRepositoryContent;

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
        final String changes = "# Exasol Test Containers 1.0.0, released \n ## Features"
                + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        when(contentMock.getChangesFile(contentMock.getVersion())).thenReturn(changes);
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
        when(contentMock.getChangesFile("1.0.0")).thenReturn("Release \n letter");
        final RepositoryHandler projectHandler = new RepositoryHandler(repositoryMock, Set.of(ReleasePlatform.GITHUB));
        assertDoesNotThrow(projectHandler::release);
    }
}