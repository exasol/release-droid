package com.exasol.release;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import com.exasol.GitRepository;
import com.exasol.GitRepositoryContent;

class GitHubReleaseMakerTest {
    @ParameterizedTest
    @ValueSource(strings = { "Release \n letter", "Release letter", "" })
    // The changes files must be checked on the validation stage. Here we throw no exception even if it's not valid
    void testMakeRelease(final String changesFileContent) {
        final String version = "1.0.0";
        final GitRepository repositoryMock = mock(GitRepository.class);
        final GitRepositoryContent contentMock = Mockito.mock(GitRepositoryContent.class);
        when(repositoryMock.getRepositoryContent()).thenReturn(contentMock);
        when(contentMock.getVersion()).thenReturn(version);
        when(contentMock.getChangesFile(version)).thenReturn(changesFileContent);
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(repositoryMock);
        assertAll(() -> assertDoesNotThrow(releaseMaker::makeRelease),
                () -> verify(contentMock, times(1)).getChangesFile(version),
                () -> verify(repositoryMock, times(1)).release(anyString(), anyString()));
    }
}