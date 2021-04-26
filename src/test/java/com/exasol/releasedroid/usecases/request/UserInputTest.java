package com.exasol.releasedroid.usecases.request;

import static com.exasol.releasedroid.usecases.request.UserInput.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.request.UserInput.Builder;

class UserInputTest {
    private static final String REPOSITORY_NAME = "repository";
    private static final String OWNER_NAME = "owner";
    private static final String PLATFORM = "github";
    private static final String GOAL = "validate";
    private static final String BRANCH = "branch";

    @Test
    void testValidUserInput() {
        final UserInput userInput = builder().owner(OWNER_NAME).repositoryName(REPOSITORY_NAME).platforms(PLATFORM)
                .goal(GOAL).branch(BRANCH).build();
        assertAll(() -> assertThat(userInput.getRepositoryName(), equalTo(OWNER_NAME + "/" + REPOSITORY_NAME)), //
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
        assertThat(exception.getMessage(), containsString("E-RD-2"));
    }

    @Test
    void testUserInputWithoutPlatforms() {
        final Builder builder = builder().goal(GOAL);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("E-RD-3"));
    }

    @Test
    void testUserInputWithoutRepositoryName() {
        final Builder builder = builder().goal(GOAL).platforms(PLATFORM);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("E-RD-4"));
    }

    @Test
    void testUserInputWithReleaseAndBranch() {
        final Builder builder = builder().goal("RELEASE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .branch(BRANCH);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("E-RD-1"));
    }
}