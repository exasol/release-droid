package com.exasol.releaserobot.github;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.PlatformValidator;
import com.exasol.releaserobot.ReleaseMaker;

class GitHubPlatformTest {
    @Test
    void testReleaseThrowsException() throws GitHubException {
        final PlatformValidator platformValidator = Mockito.mock(PlatformValidator.class);
        final ReleaseMaker releaseMaker = Mockito.mock(ReleaseMaker.class);
        doThrow(GitHubException.class).when(releaseMaker).makeRelease();
        final GitHubPlatform platform = new GitHubPlatform(releaseMaker, platformValidator);
        assertAll(() -> assertThrows(GitHubException.class, () -> platform.release(null)),
                () -> verify(releaseMaker, times(1)).makeRelease());
    }
}