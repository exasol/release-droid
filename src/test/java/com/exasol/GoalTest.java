package com.exasol;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GoalTest {
    @ParameterizedTest
    @ValueSource(strings = { "validate  ", " release" })
    void testGetGoalValid(final String goal) {
        assertThat(Goal.getGoal(goal), in(Goal.values()));
    }

    @ParameterizedTest
    @ValueSource(strings = { "valida te", " relase" })
    void testGetGoalInvalid(final String goal) {
        assertThrows(IllegalArgumentException.class, () -> Goal.getGoal(goal));
    }
}