package com.exasol.validation;

import static com.exasol.validation.GitHubPlatformValidator.GITHUB_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.github.GitHubException;
import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitBranchContent;
import com.exasol.repository.ReleaseLetter;

class GitHubPlatformValidatorTest {
    private ValidationReport validationReport;

    @BeforeEach
    void beforeEach() {
        this.validationReport = new ValidationReport();
    }

    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateContainsHeader() {
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.of("header"));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, null, this.validationReport);
        validator.validateContainsHeader(changesLetter);
        assertThat(this.validationReport.hasFailedValidations(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateDoesNotContainHeader() {
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.empty());
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, null, this.validationReport);
        validator.validateContainsHeader(changesLetter);
        assertThat(this.validationReport.getFailedValidations(), containsString("E-RR-VAL-1"));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTickets() {
        final GitHubPlatform platformMock = Mockito.mock(GitHubPlatform.class);
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(platformMock.getClosedTickets()).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, platformMock,
                this.validationReport);
        validator.validateGitHubTickets(changesLetter);
        assertThat(this.validationReport.hasFailedValidations(), equalTo(false));
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
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContent, platformMock,
                this.validationReport);
        validator.validateGitHubTickets(changesLetter);
        assertThat(this.validationReport.getFailedValidations(), containsString("E-RR-VAL-2"));
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
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContent, platformMock,
                this.validationReport);
        validator.validateGitHubTickets(changesLetter);
        assertThat(this.validationReport.hasFailedValidations(), equalTo(false));
    }

    @Test
    void testValidateWorkflowFileExists() {
        final GitBranchContent branchContentMock = Mockito.mock(GitBranchContent.class);
        when(branchContentMock.getSingleFileContentAsString(GITHUB_WORKFLOW_PATH)).thenReturn("I exist");
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContentMock, null,
                this.validationReport);
        validator.validateWorkflowFileExists();
        assertThat(this.validationReport.hasFailedValidations(), equalTo(false));
    }

    @Test
    void testValidateWorkflowFileExistsThrowsException() {
        final GitBranchContent branchContentMock = Mockito.mock(GitBranchContent.class);
        when(branchContentMock.getSingleFileContentAsString(GITHUB_WORKFLOW_PATH)).thenThrow(GitHubException.class);
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContentMock, null,
                this.validationReport);
        validator.validateWorkflowFileExists();
        assertThat(this.validationReport.getFailedValidations(), containsString("E-RR-VAL-3"));
    }
}