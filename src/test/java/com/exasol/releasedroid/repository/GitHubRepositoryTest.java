package com.exasol.releasedroid.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.github.GitHubException;
import com.exasol.releasedroid.github.GithubGateway;

@ExtendWith(MockitoExtension.class)
class GitHubRepositoryTest {
    private static final String BRANCH_NAME = "my_branch";
    private static final String LATEST_TAG = "1.0.0";
    private static final String NAME = "test-repository";
    @Mock
    private GithubGateway githubGatewayMock;
    private GitHubRepository gitHubRepository;

    @BeforeEach
    void beforeEach() {
        this.gitHubRepository = new GitHubRepository(this.githubGatewayMock, BRANCH_NAME, NAME, LATEST_TAG);
    }

    @Test
    void testGetLatestTag() {
        assertAll(() -> assertThat(this.gitHubRepository.getLatestTag().isPresent(), equalTo(true)),
                () -> assertThat(this.gitHubRepository.getLatestTag().get(), equalTo(LATEST_TAG)));
    }

    @Test
    void testGetBranchName() {
        assertThat(this.gitHubRepository.getBranchName(), equalTo(BRANCH_NAME));
    }

    @Test
    void testIsOnDefaultBranchTrue() throws GitHubException {
        when(this.githubGatewayMock.getDefaultBranch(NAME)).thenReturn(BRANCH_NAME);
        assertThat(this.gitHubRepository.isOnDefaultBranch(), equalTo(true));
    }

    @Test
    void testIsOnDefaultBranchFalse() throws GitHubException {
        when(this.githubGatewayMock.getDefaultBranch(NAME)).thenReturn("some_branch");
        assertThat(this.gitHubRepository.isOnDefaultBranch(), equalTo(false));
    }

    @Test
    void testIsOnDefaultBranchThrowsException() throws GitHubException {
        when(this.githubGatewayMock.getDefaultBranch(NAME)).thenThrow(GitHubException.class);
        assertThrows(RepositoryException.class, () -> this.gitHubRepository.isOnDefaultBranch());
    }

    @Test
    void testUpdateFileContent() throws GitHubException {
        final String filePath = "/path/to";
        final String newContent = "some content";
        final String commitMessage = "new commit";
        this.gitHubRepository.updateFileContent(filePath, newContent, commitMessage);
        verify(this.githubGatewayMock, times(1)).updateFileContent(NAME, BRANCH_NAME, filePath, newContent,
                commitMessage);
    }

    @Test
    void testUpdateFileContentThrowsException() throws GitHubException {
        final String filePath = "/path/to";
        final String newContent = "some content";
        final String commitMessage = "new commit";
        doThrow(GitHubException.class).when(this.githubGatewayMock).updateFileContent(NAME, BRANCH_NAME, filePath,
                newContent, commitMessage);
        assertThrows(RepositoryException.class,
                () -> this.gitHubRepository.updateFileContent(filePath, newContent, commitMessage));
    }

    @Test
    void testGetSingleFileContentAsString() throws GitHubException {
        final String filePath = "/path/to";
        final String fileContent = "some content";
        when(this.githubGatewayMock.getFileContent(NAME, BRANCH_NAME, filePath))
                .thenReturn(new ByteArrayInputStream(fileContent.getBytes()));
        assertThat(this.gitHubRepository.getSingleFileContentAsString(filePath), equalTo(fileContent));
    }

    @Test
    void testGetSingleFileContentAsStringThrowsException1() throws GitHubException {
        final String filePath = "/path/to";
        when(this.githubGatewayMock.getFileContent(NAME, BRANCH_NAME, filePath)).thenThrow(GitHubException.class);
        assertThrows(RepositoryException.class, () -> this.gitHubRepository.getSingleFileContentAsString(filePath));
    }
}