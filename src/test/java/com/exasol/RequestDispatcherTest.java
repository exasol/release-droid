package com.exasol;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

class RequestDispatcherTest {
    @Test
    void testGetReleasePlatformsList() {
        final RequestDispatcher dispatcher = new RequestDispatcher();
        assertThat(dispatcher.getReleasePlatformsList(new String[] { "github" }),
                equalTo(Set.of(ReleasePlatform.GITHUB)));
    }

    @Test
    void testGetReleasePlatformsListWrongPlatform() {
        final RequestDispatcher dispatcher = new RequestDispatcher();
        assertThrows(IllegalArgumentException.class,
                () -> dispatcher.getReleasePlatformsList(new String[] { "git hub" }));
    }
}