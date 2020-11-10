package com.exasol.releaserobot.usecases;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

class PlatformNameTest {
    @Test
    void testGetPlatformNamesList() {
        assertThat(PlatformName.toSet("github"), equalTo(Set.of(PlatformName.GITHUB)));
    }

    @Test
    void testGetPlatformNamesListWrongName() {
        assertThrows(IllegalArgumentException.class, () -> PlatformName.toSet(new String[] { "git hub" }));
    }
}