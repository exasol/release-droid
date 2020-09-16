package com.exasol.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.*;
import org.mockito.Mockito;

import com.exasol.repository.AbstractGitHubGitBranchContent;
import com.exasol.repository.GitBranchContent;

class AbstractGitHubGitBranchContentTest {
    @Test
    void testCreateAbstractGitHubGitRepositoryContentWithInvalidBranch() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final String branchName = "my_branch";
        when(ghRepositoryMock.getBranch(branchName)).thenThrow(IOException.class);
        assertThrows(GitHubException.class, () -> new DummyGitBranch(ghRepositoryMock, branchName));
    }

    @Test
    void testGetChangelogFile() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final GHBranch branchMock = Mockito.mock(GHBranch.class);
        final String branchName = "my_branch";
        final String textContent = "Text content";
        when(contentMock.read()).thenReturn(new ByteArrayInputStream(textContent.getBytes()));
        when(ghRepositoryMock.getBranch(branchName)).thenReturn(branchMock);
        when(branchMock.getName()).thenReturn(branchName);
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenReturn(contentMock);
        final GitBranchContent repository = new DummyGitBranch(ghRepositoryMock, branchName);
        assertThat(repository.getChangelogFile(), equalTo(textContent));
    }

    @Test
    void testGetChangelogFileThrowsException() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHBranch branchMock = Mockito.mock(GHBranch.class);
        final String branchName = "my_branch";
        when(ghRepositoryMock.getBranch(branchName)).thenReturn(branchMock);
        when(branchMock.getName()).thenReturn(branchName);
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenThrow(IOException.class);
        final GitBranchContent repository = new DummyGitBranch(ghRepositoryMock, branchName);
        assertThrows(GitHubException.class, repository::getChangelogFile);
    }

    @Test
    void testGetChangesFileWithCaching() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final GHBranch branchMock = Mockito.mock(GHBranch.class);
        final String branchName = "my_branch";
        when(contentMock.read()).thenReturn(new ByteArrayInputStream("".getBytes()));
        when(ghRepositoryMock.getBranch(branchName)).thenReturn(branchMock);
        when(branchMock.getName()).thenReturn(branchName);
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenReturn(contentMock);
        final GitBranchContent repository = new DummyGitBranch(ghRepositoryMock, branchName);
        assertAll(
                () -> assertThat(repository.getReleaseLetter(repository.getVersion()).getFileName(),
                        equalTo("changes_1.0.0.md")),
                () -> assertThat(repository.getReleaseLetter(repository.getVersion()).getFileName(),
                        equalTo("changes_1.0.0.md")),
                () -> verify(ghRepositoryMock, times(1)).getFileContent(anyString(), anyString()));
    }

    private static final class DummyGitBranch extends AbstractGitHubGitBranchContent {
        protected DummyGitBranch(final GHRepository repository, final String branch) {
            super(repository, branch);
        }

        @Override
        public String getVersion() {
            return "1.0.0";
        }

        @Override
        public Map<String, String> getDeliverables() {
            return null;
        }
    }
}