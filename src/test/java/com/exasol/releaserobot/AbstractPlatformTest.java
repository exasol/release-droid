package com.exasol.releaserobot;

import static com.exasol.releaserobot.Platform.PlatformName.GITHUB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class AbstractPlatformTest {
    @Test
    void testGetPlatformName() {
        final Platform platform = new DummyPlatform(GITHUB);
        assertThat(platform.getPlatformName(), equalTo(GITHUB));
    }

    private static class DummyPlatform extends AbstractPlatform {
        protected DummyPlatform(final PlatformName platformName) {
            super(platformName);
        }
    }
}