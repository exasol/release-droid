package com.exasol;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

class ReleasePlatformTest {
    @Test
    void testGetReleasePlatformsList() {
        assertThat(ReleasePlatform.toSet("github"), equalTo(Set.of(ReleasePlatform.GITHUB)));
    }

    @Test
    void testGetReleasePlatformsListWrongPlatform() {
        assertThrows(IllegalArgumentException.class, () -> ReleasePlatform.toSet(new String[] { "git hub" }));
    }
}