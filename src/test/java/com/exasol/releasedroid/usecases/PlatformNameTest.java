package com.exasol.releasedroid.usecases;

import static com.exasol.releasedroid.usecases.PlatformName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class PlatformNameTest {
    @Test
    void testGetPlatformNamesList() {
        assertThat(toList("github", "maven"), equalTo(List.of(MAVEN, GITHUB)));
    }

    @Test
    void testGetPlatformNamesListWrongName() {
        assertThrows(IllegalArgumentException.class, () -> toList(new String[] { "git hub" }));
    }
}