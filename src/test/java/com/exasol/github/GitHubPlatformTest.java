package com.exasol.github;

import static com.exasol.Platform.PlatformName.GITHUB;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHReleaseBuilder;
import org.kohsuke.github.GHRepository;
import org.mockito.Mockito;

class GitHubPlatformTest {
    @Test
    void testReleaseThrowsException() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHReleaseBuilder releaseBuilderMock = Mockito.mock(GHReleaseBuilder.class);
        when(releaseBuilderMock.draft(true)).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.body(anyString())).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.name(anyString())).thenReturn(releaseBuilderMock);
        when(ghRepositoryMock.createRelease(anyString())).thenReturn(releaseBuilderMock);
        when(releaseBuilderMock.create()).thenThrow(IOException.class);
        final GitHubPlatform platform = new GitHubPlatform(GITHUB, ghRepositoryMock, new GitHubUser("", ""));
        assertAll(() -> assertThrows(GitHubException.class, () -> platform.release("", "", "")),
                () -> verify(releaseBuilderMock, times(1)).draft(true),
                () -> verify(releaseBuilderMock, times(1)).name(anyString()),
                () -> verify(releaseBuilderMock, times(1)).body(anyString()),
                () -> verify(ghRepositoryMock, times(1)).createRelease(anyString()));
    }
}