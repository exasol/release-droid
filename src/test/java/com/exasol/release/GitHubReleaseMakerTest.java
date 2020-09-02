package com.exasol.release;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitRepositoryContent;
import com.exasol.repository.ReleaseChangesLetter;

class GitHubReleaseMakerTest {
    @Test
    // The changes files must be checked on the validation stage. Here we throw no exception even if it's not valid
    void testMakeRelease() {
        final String version = "1.0.0";
        final GitRepositoryContent contentMock = Mockito.mock(GitRepositoryContent.class);
        final ReleaseChangesLetter changesMock = mock(ReleaseChangesLetter.class);
        final GitHubPlatform gitHubPlatform = mock(GitHubPlatform.class);
        when(changesMock.getBody()).thenReturn(Optional.empty());
        when(contentMock.getVersion()).thenReturn(version);
        when(contentMock.getReleaseChangesLetter(version)).thenReturn(changesMock);
        final ReleaseMaker releaseMaker = new GitHubReleaseMaker(contentMock, gitHubPlatform);
        assertAll(() -> assertDoesNotThrow(releaseMaker::makeRelease),
                () -> verify(contentMock, times(1)).getReleaseChangesLetter(version),
                () -> verify(gitHubPlatform, times(1)).release(anyString(), anyString(), anyString()));
    }
}