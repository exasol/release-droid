package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.github.GitHubAPIAdapterTest.mockCommits;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.adapter.github.GitHubTag.LatestCommitException;

@ExtendWith(MockitoExtension.class)
class GitHubTagTest {

    @Mock
    private GHRepository repositoryMock;

    @Test
    void success() throws Exception {
        mockCommits(this.repositoryMock, true);
        assertDoesNotThrow(() -> createTag());
    }

    @Test
    void createRefException() throws Exception {
        mockCommits(this.repositoryMock, true);
        when(this.repositoryMock.createRef(any(), any())).thenThrow(new IOException());
        final Exception exception = assertThrows(GitHubException.class, () -> createTag());
        assertThat(exception.getMessage(), startsWith("E-RD-GH-30: Failed creating additional tag"));
    }

    @Test
    void getRefException() throws Exception {
        when(this.repositoryMock.getRef(any())).thenThrow(new IOException("inner"));
        final Exception exception = assertThrows(LatestCommitException.class, () -> createTag());
        assertThat(exception.getMessage(), startsWith("E-RD-GH-32: Failed retrieving latest commit"));

        final Throwable cause = exception.getCause();
        assertThat(cause, isA(IOException.class));
        assertThat(cause.getMessage(), equalTo("inner"));
    }

    @Test
    void noCommits() throws Exception {
        mockCommits(this.repositoryMock, false);
        final Exception exception = assertThrows(LatestCommitException.class, () -> createTag());
        assertThat(exception.getMessage(), startsWith("E-RD-GH-32: Failed retrieving latest commit"));
        assertThat(exception.getCause(), nullValue());
    }

    private void createTag() throws Exception {
        new GitHubTag(this.repositoryMock).create("v1.2.3");
    }
}
