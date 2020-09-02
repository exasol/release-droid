package com.exasol.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.*;
import org.mockito.Mockito;

import com.exasol.git.GitRepositoryContent;

class AbstractGitHubGitRepositoryContentTest {
    @Test
    void testCreateAbstractGitHubGitRepositoryContentWithInvalidBranch() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final String branchName = "my_branch";
        when(ghRepositoryMock.getBranch(branchName)).thenThrow(IOException.class);
        assertThrows(GitHubException.class, () -> new DummyGitHubRepository(ghRepositoryMock, branchName));
    }

    @Test
    void testGetChangelogFile() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final GHBranch branchMock = Mockito.mock(GHBranch.class);
        final String branchName = "my_branch";
        final String textContent = "Text content";
        when(contentMock.getContent()).thenReturn(textContent);
        when(ghRepositoryMock.getBranch(branchName)).thenReturn(branchMock);
        when(branchMock.getName()).thenReturn(branchName);
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenReturn(contentMock);
        final GitRepositoryContent repository = new DummyGitHubRepository(ghRepositoryMock, branchName);
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
        final GitRepositoryContent repository = new DummyGitHubRepository(ghRepositoryMock, branchName);
        assertThrows(GitHubException.class, repository::getChangelogFile);
    }

    @Test
    void testGetChangesFileWithCaching() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final GHBranch branchMock = Mockito.mock(GHBranch.class);
        final String branchName = "my_branch";
        when(contentMock.getContent()).thenReturn("");
        when(ghRepositoryMock.getBranch(branchName)).thenReturn(branchMock);
        when(branchMock.getName()).thenReturn(branchName);
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenReturn(contentMock);
        final GitRepositoryContent repository = new DummyGitHubRepository(ghRepositoryMock, branchName);
        assertAll(
                () -> assertThat(repository.getReleaseChangesLetter(repository.getVersion()).getFileName(),
                        equalTo("changes_1.0.0.md")),
                () -> assertThat(repository.getReleaseChangesLetter(repository.getVersion()).getFileName(),
                        equalTo("changes_1.0.0.md")),
                () -> verify(ghRepositoryMock, times(1)).getFileContent(anyString(), anyString()));
    }

    private static final class DummyGitHubRepository extends AbstractGitHubGitRepositoryContent {
        protected DummyGitHubRepository(final GHRepository repository, final String branch) {
            super(repository, branch);
        }

        @Override
        public String getVersion() {
            return "1.0.0";
        }
    }
}