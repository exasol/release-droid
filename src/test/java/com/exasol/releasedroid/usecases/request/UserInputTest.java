package com.exasol.releasedroid.usecases.request;

import static com.exasol.releasedroid.usecases.request.UserInput.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class UserInputTest {
    private static final String REPOSITORY_NAME = "repository";
    private static final String OWNER_NAME = "owner";
    private static final String PLATFORM = "github";
    private static final String GOAL = "validate";
    private static final String BRANCH = "branch";
    private static final String LOCAL_PATH = "some/path";
    private static final String LANGUAGE = "java";

    @Test
    void testValidUserInput() {
        final UserInput userInput = builder() //
                .owner(OWNER_NAME) //
                .repositoryName(REPOSITORY_NAME) //
                .goal(GOAL) //
                .platforms(PLATFORM) //
                .branch(BRANCH) //
                .language(LANGUAGE) //
                .localPath(LOCAL_PATH) //
                .build();
        assertAll( //
                () -> assertThat(userInput.getOwner(), equalTo(OWNER_NAME)), //
                () -> assertThat(userInput.getRepositoryName(), equalTo(REPOSITORY_NAME)), //
                () -> assertThat(userInput.getFullRepositoryName(), equalTo(OWNER_NAME + "/" + REPOSITORY_NAME)), //
                () -> assertThat(userInput.getGoal(), equalTo(Goal.VALIDATE)), //
                () -> assertThat(userInput.getPlatformNames(), contains(PlatformName.GITHUB)), //
                () -> assertThat(userInput.getBranch(), equalTo(BRANCH)), //
                () -> assertThat(userInput.getLanguage(), equalTo(Language.JAVA)), //
                () -> assertThat(userInput.getLocalPath(), equalTo(LOCAL_PATH)), //
                () -> assertThat(userInput.hasOwner(), equalTo(true)), //
                () -> assertThat(userInput.hasRepositoryName(), equalTo(true)), //
                () -> assertThat(userInput.hasGoal(), equalTo(true)), //
                () -> assertThat(userInput.hasPlatforms(), equalTo(true)), //
                () -> assertThat(userInput.hasLanguage(), equalTo(true)), //
                () -> assertThat(userInput.hasLocalPath(), equalTo(true)) //
        );
    }

    @Test
    void testEmptyUserInput() {
        final UserInput userInput = builder() //
                .build();
        assertAll( //
                () -> assertThat(userInput.getOwner(), equalTo(null)), //
                () -> assertThat(userInput.getRepositoryName(), equalTo(null)), //
                () -> assertThat(userInput.getGoal(), equalTo(null)), //
                () -> assertThat(userInput.getPlatformNames(), equalTo(null)), //
                () -> assertThat(userInput.getBranch(), equalTo(null)), //
                () -> assertThat(userInput.getLanguage(), equalTo(null)), //
                () -> assertThat(userInput.getLocalPath(), equalTo(null)), //
                () -> assertThat(userInput.hasOwner(), equalTo(false)), //
                () -> assertThat(userInput.hasRepositoryName(), equalTo(false)), //
                () -> assertThat(userInput.hasGoal(), equalTo(false)), //
                () -> assertThat(userInput.hasPlatforms(), equalTo(false)), //
                () -> assertThat(userInput.hasLanguage(), equalTo(false)), //
                () -> assertThat(userInput.hasLocalPath(), equalTo(false)) //
        );
    }
}