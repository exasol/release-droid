package com.exasol.release;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.git.*;

class GitHubReleaseMakerTest {
    @Test
    // The changes files must be checked on the validation stage. Here we throw no exception even if it's not valid
    void testMakeRelease() {
        final String version = "1.0.0";
        final GitRepository repositoryMock = mock(GitRepository.class);
        final GitRepositoryContent contentMock = Mockito.mock(GitRepositoryContent.class);
        final ReleaseChangesLetter changes = mock(ReleaseChangesLetter.class);
        when(changes.getBody()).thenReturn(Optional.empty());
        when(repositoryMock.getRepositoryContent(anyString())).thenReturn(contentMock);
        when(repositoryMock.getDefaultBranchName()).thenReturn("master");
        when(contentMock.getVersion()).thenReturn(version);
        when(contentMock.getReleaseChangesLetter(version)).thenReturn(changes);
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(repositoryMock);
        assertAll(() -> assertDoesNotThrow(releaseMaker::makeRelease),
                () -> verify(contentMock, times(1)).getReleaseChangesLetter(version),
                () -> verify(repositoryMock, times(1)).release(anyString(), anyString()));
    }
}