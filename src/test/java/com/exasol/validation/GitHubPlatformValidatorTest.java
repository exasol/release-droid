package com.exasol.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.github.GitHubPlatform;
import com.exasol.repository.ReleaseChangesLetter;

class GitHubPlatformValidatorTest {
    @Test
    void testValidateContainsHeader() {
        final ReleaseChangesLetter changesLetter = Mockito.mock(ReleaseChangesLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.of("header"));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, null);
        assertDoesNotThrow(() -> validator.validateContainsHeader(changesLetter));
    }

    @Test
    void testValidateDoesNotContainHeader() {
        final ReleaseChangesLetter changesLetter = Mockito.mock(ReleaseChangesLetter.class);
        when(changesLetter.getHeader()).thenReturn(Optional.empty());
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, null);
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validateContainsHeader(changesLetter));
        assertThat(exception.getMessage(), containsString("file does not contain 'Code name' section"));
    }

    @Test
    void testValidateGitHubTickets() {
        final GitHubPlatform platformMock = Mockito.mock(GitHubPlatform.class);
        final ReleaseChangesLetter changesLetter = Mockito.mock(ReleaseChangesLetter.class);
        when(platformMock.getClosedTickets()).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, platformMock);
        assertDoesNotThrow(() -> validator.validateGitHubTickets(changesLetter));
    }

    @Test
    void testValidateGitHubTicketsInvalidTickets() {
        final GitHubPlatform platformMock = Mockito.mock(GitHubPlatform.class);
        final ReleaseChangesLetter changesLetter = Mockito.mock(ReleaseChangesLetter.class);
        when(platformMock.getClosedTickets()).thenReturn(Set.of(1, 2, 3, 4));
        when(changesLetter.getTicketNumbers()).thenReturn(List.of(1, 2, 5, 6));
        final GitHubPlatformValidator validator = new GitHubPlatformValidator(null, platformMock);
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validateGitHubTickets(changesLetter));
        assertThat(exception.getMessage(), containsString("GitHub issues are not closed or do not exists: 5, 6"));
    }
}