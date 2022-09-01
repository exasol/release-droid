package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.*;
import org.kohsuke.github.GHWorkflowRun.Conclusion;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;

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

    @Tag("integration")
    @Test
    void testExecuteWorkflow() throws IOException, GitHubException {
        final String workflowName = "some_workflow.yml";
        final String defaultBranch = "main";
        final GHWorkflow workflowMock = mockWorkflow(mockWorkflowRun());
        when(this.repositoryMock.getDefaultBranch()).thenReturn(defaultBranch);
        when(this.repositoryMock.getWorkflow(anyString())).thenReturn(workflowMock);
        this.apiAdapter.executeWorkflow(REPOSITORY_NAME, workflowName, new WorkflowOptions());
        verify(workflowMock, times(1)).dispatch(defaultBranch, Map.of());
    }

    private GHWorkflowRun mockWorkflowRun() throws IOException {
        final GHWorkflowRun run = Mockito.mock(GHWorkflowRun.class);
        when(run.getHtmlUrl()).thenReturn(new URL("http://of-workflow-run"));
        when(run.getConclusion()).thenReturn(Conclusion.SUCCESS);
        return run;
    }

    @SuppressWarnings("unchecked")
    private GHWorkflow mockWorkflow(final GHWorkflowRun run) throws IOException {
        final PagedIterator<GHWorkflowRun> ptor = Mockito.mock(PagedIterator.class);
        when(ptor.hasNext()).thenReturn(run != null);
        if (run != null) {
            when(ptor.next()).thenReturn(run);
        }

        final PagedIterable<GHWorkflowRun> pable = Mockito.mock(PagedIterable.class);
        when(pable.iterator()).thenReturn(ptor);

        final GHWorkflow workflow = Mockito.mock(GHWorkflow.class);
        when(workflow.listRuns()).thenReturn(pable);
        return workflow;
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
        assertThat(this.apiAdapter.downloadArtifactAsString(REPOSITORY_NAME, artifactId), equalTo("hashsum file.jar"));
    }

    @Test
    void releaseCreateRelease() throws GitHubException, IOException {
        final String version = "4.5.6";
        final GitHubRelease release = GitHubRelease.builder() //
                .repositoryName(REPOSITORY_NAME) //
                .version(version).header("title") //
                .releaseLetter("release letter") //
                .build();

        final URL expectedHtmlUrl = mockGHRepository(this.gitHubMock, REPOSITORY_NAME);
        final String expectedTagUrl = GitHubReleaseInfo.getTagUrl(REPOSITORY_NAME, version);

        final GitHubReleaseInfo info = this.apiAdapter.createGithubRelease(release, Progress.builder().start());
        assertAll(() -> assertThat(info.getHtmlUrl(), equalTo(expectedHtmlUrl)), //
                () -> assertThat(info.isDraft(), equalTo(true)), //
                () -> assertThat(info.getTagUrl(), equalTo(expectedTagUrl)));

    }

    @Test
    void estimateDurationException() throws IOException {
        final String workflow = "workflow";
        when(this.repositoryMock.getWorkflow(workflow)).thenThrow(new IOException("sample message"));
        assertDoesNotThrow(() -> this.apiAdapter.estimateDuration(REPOSITORY_NAME, workflow));
    }

    @Test
    void estimateDuration() throws IOException {
        final String workflowName = "some_workflow.yml";
        final GHWorkflow workflowMock = mockWorkflow(null);
        when(this.repositoryMock.getWorkflow(workflowName)).thenReturn(workflowMock);
        final Estimation actual = this.apiAdapter.estimateDuration(REPOSITORY_NAME, workflowName);
        assertThat(actual.isPresent(), is(false));
    }

    private URL mockGHRepository(final GitHub gitHub, final String REPOSITORY_NAME) throws IOException {
        final URL htmlUrl = new URL("https://github.com/" + REPOSITORY_NAME + "/releases/releases/edit/untagged-123");
        final GHRelease releaseMock = mock(GHRelease.class);
        when(releaseMock.getHtmlUrl()).thenReturn(htmlUrl);
        when(releaseMock.isDraft()).thenReturn(true);

        final GHRepository repoMock = Mockito.mock(GHRepository.class);
        final GHReleaseBuilder builder = releaseBuilderMock(releaseMock);
        when(repoMock.createRelease(any())).thenReturn(builder);

        when(gitHub.getRepository(REPOSITORY_NAME)).thenReturn(repoMock);
        return htmlUrl;
    }

    private GHReleaseBuilder releaseBuilderMock(final GHRelease release) throws IOException {
        final GHReleaseBuilder builder = Mockito.mock(GHReleaseBuilder.class);
        when(builder.draft(anyBoolean())).thenReturn(builder);
        when(builder.body(any())).thenReturn(builder);
        when(builder.name(any())).thenReturn(builder);
        when(builder.create()).thenReturn(release);
        return builder;
    }
}