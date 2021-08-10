package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GitHubAPIAdapterTest {
    private static final String REPOSITORY_NAME = "test/my-repo";
    @Mock
    private GitHub gitHubMock;
    @Mock
    private GitHubConnector gitHubConnectorMock;
    @Mock
    private GHRepository repositoryMock;
    private GitHubAPIAdapter apiAdapter;

    @BeforeEach
    void beforeEach() throws IOException {
        this.apiAdapter = new GitHubAPIAdapter(this.gitHubConnectorMock);
        when(this.gitHubConnectorMock.connectToGitHub()).thenReturn(this.gitHubMock);
        when(this.gitHubMock.getRepository(REPOSITORY_NAME)).thenReturn(this.repositoryMock);
    }

    @Test
    void testExecuteWorkflow() throws IOException {
        final String workflowName = "some_workflow.yml";
        final String defaultBranch = "main";
        final GHWorkflow workflowMock = Mockito.mock(GHWorkflow.class);
        when(this.repositoryMock.getWorkflow(workflowName)).thenReturn(workflowMock);
        when(this.repositoryMock.getDefaultBranch()).thenReturn(defaultBranch);
        doThrow(IOException.class).when(workflowMock).dispatch(defaultBranch, Map.of());
        assertAll(
                () -> assertThrows(GitHubException.class,
                        () -> this.apiAdapter.executeWorkflow(REPOSITORY_NAME, workflowName)),
                () -> verify(workflowMock, times(1)).dispatch(defaultBranch, Map.of()));
    }

    @Test
    void testGetLanguage() throws GitHubException {
        final String language = "Java";
        when(this.repositoryMock.getLanguage()).thenReturn(language);
        assertThat(this.apiAdapter.getRepositoryPrimaryLanguage(REPOSITORY_NAME), equalTo(language));
    }

    @Test
    void testDownloadArtifactAsString() throws IOException, GitHubException {
        final long artifactId = 123;
        final GHArtifact artifactMock = Mockito.mock(GHArtifact.class);
        final GHRepository repositoryMock = Mockito.mock(GHRepository.class);
        when(this.gitHubMock.getRepository(REPOSITORY_NAME)).thenReturn(repositoryMock);
        when(repositoryMock.getArtifact(artifactId)).thenReturn(artifactMock);
        when(artifactMock.download(any())).thenReturn("hashsum file.jar");
        assertThat(this.apiAdapter.downloadArtifactAsString(REPOSITORY_NAME, artifactId), equalTo("hashsum file.jar" +
                ""));
    }
}