package com.exasol.releaserobot;

import static com.exasol.releaserobot.UserInput.Builder;
import static com.exasol.releaserobot.UserInput.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UserInputTest {
    private static final String OWNER = "owner";
    private static final String REPOSITORY_NAME = "repository";
    private static final String PLATFORM = "github";
    private static final String GOAL = "validate";
    private static final String BRANCH = "branch";

    @Test
    void testValidUserInput() {
        final UserInput userInput = builder().repositoryOwner(OWNER).repositoryName(REPOSITORY_NAME).platforms(PLATFORM)
                .goal(GOAL).gitBranch(BRANCH).build();
        assertAll(() -> assertThat(userInput.getRepositoryOwner(), equalTo(OWNER)), //
                () -> assertThat(userInput.getRepositoryName(), equalTo(REPOSITORY_NAME)), //
                () -> assertThat(userInput.getGoal(), equalTo(Goal.VALIDATE)), //
                () -> assertThat(userInput.getPlatformNames(), contains(Platform.PlatformName.GITHUB)), //
                () -> assertThat(userInput.hasGitBranch(), equalTo(true)), //
                () -> assertThat(userInput.getGitBranch(), equalTo(BRANCH)) //
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
    void testUserInputWithoutRepositoryOwner() {
        final Builder builder = builder().goal(GOAL).platforms(PLATFORM).repositoryName(REPOSITORY_NAME);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("E-RR-5"));
    }

    @Test
    void testUserInputWithReleaseAndBranch() {
        final Builder builder = builder().goal("RELEASE").platforms(PLATFORM).repositoryOwner(OWNER)
                .repositoryName(REPOSITORY_NAME).gitBranch(BRANCH);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("E-RR-1"));
    }
}