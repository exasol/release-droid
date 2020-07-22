package com.exasol;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.release.ReleasePlatform;

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

    @ParameterizedTest
    @ValueSource(strings = { "validate  ", " release" })
    void testGetGoalValid(final String goal) {
        final RequestDispatcher dispatcher = new RequestDispatcher();
        assertThat(dispatcher.getGoal(goal), in(Goal.values()));
    }

    @ParameterizedTest
    @ValueSource(strings = { "valida te", " relase" })
    void testGetGoalInvalid(final String goal) {
        final RequestDispatcher dispatcher = new RequestDispatcher();
        assertThrows(IllegalArgumentException.class, () -> dispatcher.getGoal(goal));
    }
}