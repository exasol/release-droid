package com.exasol.release;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.github.GitHubRepository;

class GitHubReleaseMakerTest {
    @ParameterizedTest
    @ValueSource(strings = { "Release \n letter", "Release letter", "" })
    // The changes files must be checked on the validation stage. Here we throw no exception even if it's not valid
    void testMakeRelease(String changesFileContent) {
        final String version = "1.0.0";
        final GitHubRepository gitHubRepositoryMock = mock(GitHubRepository.class);
        when(gitHubRepositoryMock.getVersion()).thenReturn(version);
        when(gitHubRepositoryMock.getChangesFile(version)).thenReturn(changesFileContent);
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(gitHubRepositoryMock);
        assertAll(() -> assertDoesNotThrow(releaseMaker::makeRelease),
                () -> verify(gitHubRepositoryMock, times(1)).getChangesFile(version),
                () -> verify(gitHubRepositoryMock, times(1)).release(anyString(), anyString(), anyString()));
    }
}