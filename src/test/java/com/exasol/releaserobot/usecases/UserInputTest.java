package com.exasol.releaserobot.usecases;

import static com.exasol.releaserobot.usecases.UserInput.Builder;
import static com.exasol.releaserobot.usecases.UserInput.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UserInputTest {
    private static final String REPOSITORY_NAME = "owner/repository";
    private static final String PLATFORM = "github";
    private static final String GOAL = "validate";
    private static final String BRANCH = "branch";

    @Test
    void testValidUserInput() {
        final UserInput userInput = builder().repositoryName(REPOSITORY_NAME).platforms(PLATFORM).goal(GOAL)
                .branch(BRANCH).build();
        assertAll(() -> assertThat(userInput.getRepositoryName(), equalTo(REPOSITORY_NAME)), //
                () -> assertThat(userInput.getGoal(), equalTo(Goal.VALIDATE)), //
                () -> assertThat(userInput.getPlatformNames(), contains(PlatformName.GITHUB)), //
                () -> assertThat(userInput.hasBranch(), equalTo(true)), //
                () -> assertThat(userInput.getBranch(), equalTo(BRANCH)) //
        );
    }

    @Test
    void testUserInputWithoutGoal() {
        final Builder builder = builder();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("E-RR-2"));
    }

    @Test
    void testUserInputWithoutPlatforms() {
        final Builder builder = builder().goal(GOAL);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("E-RR-3"));
    }

    @Test
    void testUserInputWithoutRepositoryName() {
        final Builder builder = builder().goal(GOAL).platforms(PLATFORM);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("E-RR-4"));
    }

    @Test
    void testUserInputWithReleaseAndBranch() {
        final Builder builder = builder().goal("RELEASE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .branch(BRANCH);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("E-RR-1"));
    }
}