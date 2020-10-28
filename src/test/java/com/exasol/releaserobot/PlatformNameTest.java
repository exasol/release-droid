package com.exasol.releaserobot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.exasol.releaserobot.Platform.PlatformName;

class PlatformNameTest {
    @Test
    void testGetPlatformNamesList() {
        assertThat(PlatformName.toSet("github"), equalTo(Set.of(PlatformName.GITHUB)));
    }

    @Test
    void testGetPlatformNamesListWrongName() {
        assertThrows(IllegalArgumentException.class, () -> PlatformName.toSet(new String[] { "git hub" }));
    }

    @Test
    void testAvailablePlatformNames() {
        assertThat(PlatformName.availablePlatformNames(), containsInAnyOrder("maven", "github"));
    }
}