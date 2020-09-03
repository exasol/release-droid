package com.exasol.release;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitRepositoryContent;
import com.exasol.repository.ReleaseLetter;

class GitHubReleaseMakerTest {
    @Test
    // The changes files must be checked on the validation stage. Here we throw no exception even if it's not valid
    void testMakeRelease() {
        final String version = "1.0.0";
        final GitRepositoryContent contentMock = Mockito.mock(GitRepositoryContent.class);
        final ReleaseLetter changesMock = mock(ReleaseLetter.class);
        final GitHubPlatform gitHubPlatform = mock(GitHubPlatform.class);
        when(changesMock.getBody()).thenReturn(Optional.empty());
        when(contentMock.getVersion()).thenReturn(version);
        when(contentMock.getReleaseLetter(version)).thenReturn(changesMock);
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(contentMock, gitHubPlatform);
        assertAll(() -> assertDoesNotThrow(releaseMaker::makeRelease),
                () -> verify(contentMock, times(1)).getReleaseLetter(version),
                () -> verify(gitHubPlatform, times(1)).release(anyString(), anyString(), anyString()));
    }
}