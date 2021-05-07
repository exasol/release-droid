package com.exasol.releasedroid.main;

import static com.exasol.releasedroid.usecases.request.UserInput.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.request.UserInput;

class ReleaseDroidTest {
    private static final String REPOSITORY_NAME = "repository";
    private static final String PLATFORM = "github";
    private static final String GOAL = "validate";
    private static final String BRANCH = "branch";
    private static final String LOCAL_PATH = "some/path";
    private final ReleaseDroid releaseDroid = ReleaseDroid.of(null);

    @Test
    void testUserInputWithoutGoal() {
        final UserInput userInput = builder().build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-2: Please specify a mandatory parameter 'goal'"));
    }

    @Test
    void testUserInputWithoutPlatforms() {
        final UserInput userInput = builder().goal(GOAL).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-2: Please specify a mandatory parameter 'platforms'"));
    }

    @Test
    void testUserInputWithoutRepositoryName() {
        final UserInput userInput = builder().goal(GOAL).platforms(PLATFORM).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(),
                containsString("E-RD-2: Please specify a mandatory parameter 'repository name'"));
    }

    @Test
    void testUserInputWithReleaseAndBranch() {
        final UserInput userInput = builder().goal("RELEASE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .branch(BRANCH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-1"));
    }

    @Test
    void testUserInputWithLocalPathAndBranch() {
        final UserInput userInput = builder().goal("VALIDATE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .branch(BRANCH).localPath(LOCAL_PATH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-6"));
    }

    @Test
    void testUserInputWithLocalPathAndGoalRelease() {
        final UserInput userInput = builder().goal("RELEASE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .localPath(LOCAL_PATH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-6"));
    }
}