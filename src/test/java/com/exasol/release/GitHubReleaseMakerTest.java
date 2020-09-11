package com.exasol.release;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitBranchContent;
import com.exasol.repository.ReleaseLetter;

class GitHubReleaseMakerTest {
    @Test
    // The changes files must be checked on the validation stage. Here we throw no exception even if it's not valid
    // [utest->dsn~create-new-github-release~1]
    // [utest->dsn~retrieve-github-release-header-from-release-letter~1]
    // [utest->dsn~retrieve-github-release-body-from-release-letter~1]
    void testMakeRelease() {
        final String version = "1.0.0";
        final GitBranchContent contentMock = Mockito.mock(GitBranchContent.class);
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