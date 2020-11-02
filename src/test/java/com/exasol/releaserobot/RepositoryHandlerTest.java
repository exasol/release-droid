package com.exasol.releaserobot;

import static com.exasol.releaserobot.Platform.PlatformName.GITHUB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.github.GitHubPlatform;
import com.exasol.releaserobot.report.ValidationReport;
import com.exasol.releaserobot.repository.*;

class RepositoryHandlerTest {
    @Test
    void testValidate() {
        final GitHubGitRepository repositoryMock = Mockito.mock(GitHubGitRepository.class);
        final GitBranchContent contentMock = Mockito.mock(GitBranchContent.class);
        final GitHubPlatform platform = Mockito.mock(GitHubPlatform.class);
        when(platform.getPlatformName()).thenReturn(GITHUB);
        when(repositoryMock.getRepositoryContent(anyString())).thenReturn(contentMock);
        when(repositoryMock.getDefaultBranchName()).thenReturn("master");
        when(repositoryMock.getLatestTag()).thenReturn(Optional.of("0.5.1"));
        when(contentMock.getVersion()).thenReturn("1.0.0");
        when(contentMock.getChangelogFile()).thenReturn("[1.0.0](changes_1.0.0.md)");
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("file").versionNumber("1.0.0")
                .releaseDate(LocalDate.now()).body("## Features").header("Test header").build();
        when(contentMock.getReleaseLetter(contentMock.getVersion())).thenReturn(releaseLetter);
        final RepositoryHandler projectHandler = new RepositoryHandler(repositoryMock, Set.of(platform));
        final ValidationReport validate = projectHandler.validate();
        assertThat(validate.hasFailures(), equalTo(false));
    }
}