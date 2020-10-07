package com.exasol.validation;

import static com.exasol.validation.GitHubPlatformValidator.GITHUB_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.github.GitHubException;
import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitBranchContent;
import com.exasol.repository.ReleaseLetter;

class GitHubPlatformValidatorTest {
    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateContainsHeader() {
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.of("header"));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, null);
        assertDoesNotThrow(() -> validator.validateContainsHeader(changesLetter));
    }

    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateDoesNotContainHeader() {
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.empty());
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, null);
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validateContainsHeader(changesLetter));
        assertThat(exception.getMessage(), containsString("E-RR-VAL-1"));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTickets() {
        final GitHubPlatform platformMock = Mockito.mock(GitHubPlatform.class);
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(platformMock.getClosedTickets()).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, platformMock);
        assertDoesNotThrow(() -> validator.validateGitHubTickets(changesLetter));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTicketsInvalidTicketsOnDefaultBranch() {
        final GitHubPlatform platformMock = Mockito.mock(GitHubPlatform.class);
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        final GitBranchContent branchContent = Mockito.mock(GitBranchContent.class);
        when(branchContent.isDefaultBranch()).thenReturn(true);
        when(platformMock.getClosedTickets()).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2, 5, 6));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContent, platformMock);
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validateGitHubTickets(changesLetter));
        assertThat(exception.getMessage(), containsString("E-RR-VAL-2"));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTicketsInvalidTickets() {
        final GitHubPlatform platformMock = Mockito.mock(GitHubPlatform.class);
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        final GitBranchContent branchContent = Mockito.mock(GitBranchContent.class);
        when(branchContent.isDefaultBranch()).thenReturn(false);
        when(platformMock.getClosedTickets()).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2, 5, 6));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContent, platformMock);
        assertDoesNotThrow(() -> validator.validateGitHubTickets(changesLetter));
    }

    @Test
    void testValidateWorkflowFileExists() {
        final GitBranchContent branchContentMock = Mockito.mock(GitBranchContent.class);
        when(branchContentMock.getSingleFileContentAsString(GITHUB_WORKFLOW_PATH)).thenReturn("I exist");
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContentMock, null);
        assertDoesNotThrow(validator::validateWorkflowFileExists);
    }

    @Test
    void testValidateWorkflowFileExistsThrowsException() {
        final GitBranchContent branchContentMock = Mockito.mock(GitBranchContent.class);
        when(branchContentMock.getSingleFileContentAsString(GITHUB_WORKFLOW_PATH)).thenThrow(GitHubException.class);
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContentMock, null);
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                validator::validateWorkflowFileExists);
        assertThat(exception.getMessage(), containsString("E-RR-VAL-3"));
    }
}