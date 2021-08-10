package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.github.GitHubConstants.GITHUB_UPLOAD_ASSETS_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;

@ExtendWith(MockitoExtension.class)
class GitHubPlatformValidatorTest {
    private static final String VERSION = "1.0.1";
    @Mock
    private Repository repositoryMock;
    @Mock
    private ReleaseLetter releaseLetterMock;
    @Mock
    private GitHubGateway githubGatewayMock;
    private GitHubPlatformValidator validator;

    @BeforeEach
    void beforeEach() {
        when(this.repositoryMock.getVersion()).thenReturn(VERSION);
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(this.releaseLetterMock);
        this.validator = new GitHubPlatformValidator(this.repositoryMock, this.githubGatewayMock);
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidationSuccessful() throws GitHubException {
        when(this.repositoryMock.getRepositoryValidators()).thenReturn(List.of());
        when(this.repositoryMock.hasFile(GITHUB_UPLOAD_ASSETS_WORKFLOW_PATH)).thenReturn(false);
        when(this.releaseLetterMock.getHeader()).thenReturn(Optional.of("header"));
        when(this.githubGatewayMock.getClosedTickets(any())).thenReturn(Set.of(1, 2, 3, 4));
        when(this.releaseLetterMock.getTicketNumbers()).thenReturn(List.of(1, 2));
        assertFalse(this.validator.validate().hasFailures());
    }

    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateContainCodeNameEmptyOptional() {
        when(this.releaseLetterMock.getHeader()).thenReturn(Optional.empty());
        final Report report = this.validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-GH-21")));
    }

    @Test
    // [utest->dsn~validate-release-letter~1]
    void testValidateContainCodeNameEmptyString() {
        when(this.releaseLetterMock.getHeader()).thenReturn(Optional.of(""));
        final Report report = this.validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-GH-21")));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTicketsInvalidTicketsOnDefaultBranch() throws GitHubException {
        when(this.repositoryMock.isOnDefaultBranch()).thenReturn(true);
        when(this.githubGatewayMock.getClosedTickets(any())).thenReturn(Set.of(1, 2, 3, 4));
        when(this.releaseLetterMock.getTicketNumbers()).thenReturn(List.of(1, 2, 5, 6));
        final Report report = this.validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString(
                        "E-RD-GH-23: Some of the mentioned GitHub issues are not closed or do not exists: 5, 6")));
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    // [utest->dsn~validate-github-issues-are-closed~1]
    void testValidateGitHubTicketsOnUserSpecifiedBranch() throws GitHubException {
        when(this.releaseLetterMock.getHeader()).thenReturn(Optional.of("header"));
        when(this.repositoryMock.isOnDefaultBranch()).thenReturn(false);
        when(this.githubGatewayMock.getClosedTickets(any())).thenReturn(Set.of(1, 2, 3, 4));
        when(this.releaseLetterMock.getTicketNumbers()).thenReturn(List.of(1, 2, 5, 6));
        final Report report = this.validator.validate();
        assertFalse(report.hasFailures());
    }

    @Test
    // [utest->dsn~validate-github-issues-exists~1]
    void testValidateGitHubTicketsCannotRetrieveTickets() throws GitHubException {
        when(this.githubGatewayMock.getClosedTickets(any())).thenThrow(GitHubException.class);
        final Report report = this.validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-GH-22")));
    }
}