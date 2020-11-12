package com.exasol.releaserobot.github;

import static com.exasol.releaserobot.github.GitHubPlatformValidator.GITHUB_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.repository.GitRepositoryException;
import com.exasol.releaserobot.repository.ReleaseLetter;
import com.exasol.releaserobot.usecases.Report;
import com.exasol.releaserobot.usecases.Repository;

class GitHubPlatformValidatorTest {
    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateContainsHeader() {
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.of("header"));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null);
        final Report report = validator.validateContainsHeader(changesLetter);
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateContainHeaderFails() {
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.empty());
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null);
        final Report report = validator.validateContainsHeader(changesLetter);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-1")));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTickets() throws GitHubException {
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        final Repository repositoryMock = Mockito.mock(Repository.class);
        when(githubGateway.getClosedTickets(anyString())).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(githubGateway);
        final Report report = validator.validateGitHubTickets(repositoryMock, changesLetter);
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTicketsInvalidTicketsOnDefaultBranch() throws GitHubException {
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        final ReleaseLetter changesLetter = Mockito.mock(ReleaseLetter.class);
        final Repository repositoryMock = Mockito.mock(Repository.class);
        when(repositoryMock.isOnDefaultBranch()).thenReturn(true);
        when(githubGateway.getClosedTickets(any())).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2, 5, 6));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(githubGateway);
        final Report report = validator.validateGitHubTickets(repositoryMock, changesLetter);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-2")));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTicketsOnUserSpecifiedBranch() throws GitHubException {
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        final ReleaseLetter releaseLetter = Mockito.mock(ReleaseLetter.class);
        final Repository repositoryMock = Mockito.mock(Repository.class);
        when(repositoryMock.isOnDefaultBranch()).thenReturn(false);
        when(githubGateway.getClosedTickets(any())).thenReturn(Set.of(1, 2, 3, 4));
        when(releaseLetter.getTicketNumbers()).thenReturn(List.of(1, 2, 5, 6));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(githubGateway);
        final Report report = validator.validateGitHubTickets(repositoryMock, releaseLetter);
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    void testValidateGitHubTicketsCannotRetrieveTickets() throws GitHubException {
        final GithubGateway githubGateway = Mockito.mock(GithubGateway.class);
        final Repository repositoryMock = Mockito.mock(Repository.class);
        final ReleaseLetter releaseLetter = Mockito.mock(ReleaseLetter.class);
        when(repositoryMock.getFullName()).thenReturn("name");
        when(githubGateway.getClosedTickets("name")).thenThrow(GitHubException.class);
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(githubGateway);
        final Report report = validator.validateGitHubTickets(repositoryMock, releaseLetter);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-10")));
    }

    @Test
    // [utest->dsn~validate-github-workflow-exists~1]
    void testValidateWorkflowFile() {
        final Repository repositoryMock = Mockito.mock(Repository.class);
        when(repositoryMock.getSingleFileContentAsString(GITHUB_WORKFLOW_PATH)).thenReturn("I exist");
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null);
        final Report report = validator.validateFileExists(repositoryMock, GITHUB_WORKFLOW_PATH, "file");
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-github-workflow-exists~1]
    void testValidateWorkflowFileFails() {
        final Repository repositoryMock = Mockito.mock(Repository.class);
        when(repositoryMock.getSingleFileContentAsString(GITHUB_WORKFLOW_PATH)).thenThrow(GitRepositoryException.class);
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null);
        final Report report = validator.validateFileExists(repositoryMock, GITHUB_WORKFLOW_PATH, "file");
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-9")));
    }
}