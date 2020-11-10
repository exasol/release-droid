package com.exasol.releaserobot.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.exasol.releaserobot.usecases.UserInput;

class UserInputParserTest {
    private final UserInputParser userInputParser = new UserInputParser();

    @Test
    void testParseUserInput() {
        final UserInput expected = UserInput.builder().repositoryName("exasol/testing-release-robot")
                .branch("some_branch").goal("validate").platforms("github", "maven").build();
        final String[] args = new String[] { "-name", "testing-release-robot", "-goal", "validate", "-platforms",
                "github,maven", "-branch", "some_branch" };
        assertThat(this.userInputParser.parseUserInput(args), equalTo(expected));
    }

    @Test
    void testParseUserInputWithoutBranch() {
        final UserInput expected = UserInput.builder().repositoryName("exasol/testing-release-robot").goal("validate")
                .platforms("github", "maven").build();
        final String[] args = new String[] { "-name", "testing-release-robot", "-goal", "validate", "-platforms",
                "github,maven" };
        assertThat(this.userInputParser.parseUserInput(args), equalTo(expected));
    }

    @Test
    void testParseUserInputMissingRepositoryNameArgument() {
        final String[] args = new String[] { "-name", "-goal", "validate", "-platforms", "github,maven", "-branch",
                "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-RUN-1: Missing argument for option: n"));
    }

    @Test
    void testParseUserInputMissingRepositoryNameOption() {
        final String[] args = new String[] { "-goal", "validate", "-platforms", "github,maven", "-branch",
                "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-RUN-1: Missing required option: n"));
    }

    @Test
    void testParseUserInputMissingGoalArgument() {
        final String[] args = new String[] { "-name", "testing-release-robot", "-goal", "-platforms", "github,maven",
                "-branch", "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-RUN-1: Missing argument for option: g"));
    }

    @Test
    void testParseUserInputMissingGoalOption() {
        final String[] args = new String[] { "-name", "testing-release-robot", "-platforms", "github,maven", "-branch",
                "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-RUN-1: Missing required option: g"));
    }

    @Test
    void testParseUserInputMissingPlatformsArgument() {
        final String[] args = new String[] { "-name", "testing-release-robot", "-goal", "validate", "-platforms",
                "-branch", "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-RUN-1: Missing argument for option: p"));
    }

    @Test
    void testParseUserInputMissingPlatformsOption() {
        final String[] args = new String[] { "-name", "testing-release-robot", "-goal", "validate", "-branch",
                "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-RUN-1: Missing required option: p"));
    }
}