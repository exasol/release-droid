package com.exasol.releasedroid.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.UserInput;

class UserInputParserTest {
    private final UserInputParser userInputParser = new UserInputParser();

    @Test
    void testParseUserInput() {
        final UserInput expected = UserInput.builder().repositoryName("testing-release-droid").branch("some_branch")
                .goal("validate").platforms("github", "maven").build();
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "validate", "-platforms",
                "github,maven", "-branch", "some_branch" };
        assertThat(this.userInputParser.parseUserInput(args), equalTo(expected));
    }

    @Test
    void testParseUserInputWithRepeatedArgument() {
        final UserInput expected = UserInput.builder().repositoryName("testing-release-droid").branch("some_branch")
                .goal("validate").platforms("github", "maven").build();
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "validate", "-platforms",
                "github", "-platforms", "maven", "-branch", "some_branch" };
        assertThat(this.userInputParser.parseUserInput(args), equalTo(expected));
    }

    @Test
    void testParseUserInputWithoutBranch() {
        final UserInput expected = UserInput.builder().repositoryName("testing-release-droid").goal("validate")
                .platforms("github", "maven").build();
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "validate", "-platforms",
                "github,maven" };
        assertThat(this.userInputParser.parseUserInput(args), equalTo(expected));
    }

    @Test
    void testParseUserInputWithLocal() {
        final UserInput expected = UserInput.builder().repositoryName("testing-release-droid").goal("validate")
                .platforms("github", "maven").localPath("../test-folder").build();
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "validate", "-platforms",
                "github,maven", "-local", "../test-folder" };
        assertThat(this.userInputParser.parseUserInput(args), equalTo(expected));
    }

    @Test
    void testParseUserInputWithLocalAndReleaseGoal() {
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "release", "-platforms",
                "github,maven", "-local", "./test-folder" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(),
                containsString("E-RR-6: The 'local' argument can't be used together with 'branch' or RELEASE 'goal'"));
    }

    @Test
    void testParseUserInputWithLocalAndBranch() {
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "validate", "-platforms",
                "github,maven", "-local", "./test-folder", "-branch", "my-branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(),
                containsString("E-RR-6: The 'local' argument can't be used together with 'branch' or RELEASE 'goal'"));
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
        assertThat(exception.getMessage(), containsString("E-RR-4"));
    }

    @Test
    void testParseUserInputMissingGoalArgument() {
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "-platforms", "github,maven",
                "-branch", "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-RUN-1: Missing argument for option: g"));
    }

    @Test
    void testParseUserInputMissingGoalOption() {
        final String[] args = new String[] { "-name", "testing-release-droid", "-platforms", "github,maven", "-branch",
                "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-2"));
    }

    @Test
    void testParseUserInputMissingPlatformsArgument() {
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "validate", "-platforms",
                "-branch", "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-RUN-1: Missing argument for option: p"));
    }

    @Test
    void testParseUserInputMissingPlatformsOption() {
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "validate", "-branch",
                "some_branch" };
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.userInputParser.parseUserInput(args));
        assertThat(exception.getMessage(), containsString("E-RR-3"));
    }

    @Test
    void testParseUserInputWithLanguage() {
        final UserInput expected = UserInput.builder().repositoryName("testing-release-droid").goal("validate")
                .platforms("github", "maven").language("JAVA").build();
        final String[] args = new String[] { "-name", "testing-release-droid", "-goal", "validate", "-platforms",
                "github,maven", "-language", "JAVA" };
        assertThat(this.userInputParser.parseUserInput(args), equalTo(expected));
    }

    @Test
    void testParseUserWithShortTagsLocal() {
        final UserInput expected = UserInput.builder().repositoryName("testing-release-droid").goal("validate")
                .platforms("github", "maven").language("JAVA").localPath("./some/path/").build();
        final String[] args = new String[] { "-n", "testing-release-droid", "-g", "validate", "-p", "github,maven",
                "-lg", "JAVA", "-l", "./some/path/" };
        assertThat(this.userInputParser.parseUserInput(args), equalTo(expected));
    }

    @Test
    void testParseUserWithShortTagsBranch() {
        final UserInput expected = UserInput.builder().repositoryName("testing-release-droid").goal("validate")
                .platforms("github", "maven").language("JAVA").branch("someBranch").build();
        final String[] args = new String[] { "-n", "testing-release-droid", "-g", "validate", "-p", "github,maven",
                "-lg", "JAVA", "-b", "someBranch" };
        assertThat(this.userInputParser.parseUserInput(args), equalTo(expected));
    }
}