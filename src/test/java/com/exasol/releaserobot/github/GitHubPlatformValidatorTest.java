package com.exasol.releaserobot.github;

import static com.exasol.releaserobot.github.GitHubPlatformValidator.GITHUB_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.repository.*;

class GitHubPlatformValidatorTest {
    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateContainsHeader() {
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.of("header"));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, null);
        validator.validateContainsHeader(changesLetter);
        assertThat(validator.validationResults.get(0).isSuccessful(), equalTo(true));
    }

    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateDoesNotContainHeader() {
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.empty());
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, null);
        validator.validateContainsHeader(changesLetter);
        assertAll(() -> assertThat(validator.validationResults.get(0).isSuccessful(), equalTo(false)),
                () -> assertThat(validator.validationResults.get(0).toString(), containsString("E-RR-VAL-1")));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTickets() throws GitHubException {
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(githubGateway.getClosedTickets()).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, githubGateway);
        validator.validateGitHubTickets(changesLetter);
        assertThat(validator.validationResults.get(0).isSuccessful(), equalTo(true));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTicketsInvalidTicketsOnDefaultBranch() throws GitHubException {
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        final GitBranchContent branchContent = Mockito.mock(GitBranchContent.class);
        when(branchContent.isDefaultBranch()).thenReturn(true);
        when(githubGateway.getClosedTickets()).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2, 5, 6));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContent, githubGateway);
        validator.validateGitHubTickets(changesLetter);
        assertAll(() -> assertThat(validator.validationResults.get(0).isSuccessful(), equalTo(false)),
                () -> assertThat(validator.validationResults.get(0).toString(), containsString("E-RR-VAL-2")));

    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTicketsInvalidTickets() throws GitHubException {
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        final GitBranchContent branchContent = Mockito.mock(GitBranchContent.class);
        when(branchContent.isDefaultBranch()).thenReturn(false);
        when(githubGateway.getClosedTickets()).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2, 5, 6));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContent, githubGateway);
        validator.validateGitHubTickets(changesLetter);
        assertThat(validator.validationResults.get(0).isSuccessful(), equalTo(true));
    }

    @Test
    void testValidateWorkflowFileExists() {
        final GitBranchContent branchContentMock = Mockito.mock(GitBranchContent.class);
        when(branchContentMock.getSingleFileContentAsString(GITHUB_WORKFLOW_PATH)).thenReturn("I exist");
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContentMock, null);
        validator.validateFileExists(GITHUB_WORKFLOW_PATH, "file");
        assertThat(validator.validationResults.get(0).isSuccessful(), equalTo(true));
    }

    @Test
    void testValidateWorkflowFileExistsThrowsException() {
        final GitBranchContent branchContentMock = Mockito.mock(GitBranchContent.class);
        when(branchContentMock.getSingleFileContentAsString(GITHUB_WORKFLOW_PATH))
                .thenThrow(GitRepositoryException.class);
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(branchContentMock, null);
        validator.validateFileExists(GITHUB_WORKFLOW_PATH, "file");
        assertAll(() -> assertThat(validator.validationResults.get(0).isSuccessful(), equalTo(false)),
                () -> assertThat(validator.validationResults.get(0).toString(), containsString("E-RR-VAL-3")));
    }
}