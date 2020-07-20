package com.exasol.github;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.release.ReleasePlatform;

class RepositoryHandlerTest {
    @Test
    void testValidate() {
        final GitHubRepository repositoryMock = Mockito.mock(GitHubRepository.class);
        when(repositoryMock.getVersion()).thenReturn("1.0.0");
        when(repositoryMock.getLatestReleaseVersion()).thenReturn(Optional.of("0.5.1"));
        when(repositoryMock.getChangelogFile()).thenReturn("[1.0.0](changes-1.0.0.md)");
        final String changes = "# Exasol Test Containers 1.0.0, released "
                + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        when(repositoryMock.getChangesFile()).thenReturn(changes);
        final RepositoryHandler repositoryHandler = new RepositoryHandler(repositoryMock,
                Set.of(ReleasePlatform.GITHUB));
        assertDoesNotThrow(repositoryHandler::validate);
    }
}