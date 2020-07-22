package com.exasol.release;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.exasol.github.GitHubRepository;

class ReleaseMakerTest {
    @Test
    void testMakeRelease() {
        final GitHubRepository gitHubRepository = mock(GitHubRepository.class);
        when(gitHubRepository.getChangesFile()).thenReturn("Release \n letter");
        final ReleaseMaker releaseMaker = new ReleaseMaker(gitHubRepository);
        assertAll(() -> assertDoesNotThrow(() -> releaseMaker.makeRelease(ReleasePlatform.GITHUB)),
                () -> verify(gitHubRepository, times(1)).getChangesFile(),
                () -> verify(gitHubRepository, times(1)).release(anyString(), anyString()));
    }
}