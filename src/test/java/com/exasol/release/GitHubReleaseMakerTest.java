package com.exasol.release;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.exasol.github.GitHubRepository;

class GitHubReleaseMakerTest {
    @Test
    void testMakeRelease() {
        final String version = "1.0.0";
        final GitHubRepository gitHubRepositoryMock = mock(GitHubRepository.class);
        when(gitHubRepositoryMock.getVersion()).thenReturn(version);
        when(gitHubRepositoryMock.getChangesFile(version)).thenReturn("Release \n letter");
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(gitHubRepositoryMock);
        assertAll(() -> assertDoesNotThrow(releaseMaker::makeRelease),
                () -> verify(gitHubRepositoryMock, times(1)).getChangesFile(version),
                () -> verify(gitHubRepositoryMock, times(1)).release(anyString(), anyString(), anyString()));
    }
}