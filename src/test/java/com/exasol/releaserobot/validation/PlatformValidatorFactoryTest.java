package com.exasol.releaserobot.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.Platform;
import com.exasol.releaserobot.github.GitHubPlatform;

class PlatformValidatorFactoryTest {
    @Test
    void testCreateProjectValidatorGitHub() {
        final GitHubPlatform platform = Mockito.mock(GitHubPlatform.class);
        when(platform.getPlatformName()).thenReturn(Platform.PlatformName.GITHUB);
        assertThat(PlatformValidatorFactory.createPlatformValidator(null, platform, null),
                instanceOf(GitHubPlatformValidator.class));
    }
}