package com.exasol.releaserobot;

import static com.exasol.releaserobot.Platform.PlatformName.GITHUB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;

import com.exasol.releaserobot.github.GitHubUser;

class AbstractPlatformTest {
    @Test
    void testGetPlatformName() {
        final Platform platform = new DummyPlatform(GITHUB, null, null);
        assertThat(platform.getPlatformName(), equalTo(GITHUB));
    }

    private static class DummyPlatform extends AbstractPlatform {
        protected DummyPlatform(final PlatformName platformName, final GHRepository repository,
                final GitHubUser gitHubUser) {
            super(platformName, repository, gitHubUser);
        }
    }
}