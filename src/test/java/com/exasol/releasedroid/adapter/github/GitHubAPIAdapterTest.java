package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kohsuke.github.*;
import org.kohsuke.github.GHWorkflowRun.Conclusion;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;

@ExtendWith(MockitoExtension.class)
class GitHubAPIAdapterTest {

    private static final String REPOSITORY_NAME = "test/my-repo";
    @Mock
    private GitHub gitHubMock;
    @Mock
    private GitHubConnector connectorMock;
    @Mock
    private GHRepository repositoryMock;
    private GitHubAPIAdapter apiAdapter;

    @BeforeEach
    void beforeEach() throws IOException {
        this.apiAdapter = new GitHubAPIAdapter(this.connectorMock, Duration.ofMillis(10));
        when(this.connectorMock.connectToGitHub()).thenReturn(this.gitHubMock);
        when(this.gitHubMock.getRepository(REPOSITORY_NAME)).thenReturn(this.repositoryMock);
    }

    @Tag("integration")
    @Test
    void testExecuteWorkflow() throws IOException, GitHubException, URISyntaxException {
        final String workflowName = "some_workflow.yml";
        final String defaultBranch = "main";
        final GHWorkflow workflowMock = mockWorkflow(mockWorkflowRun());
        when(this.repositoryMock.getDefaultBranch()).thenReturn(defaultBranch);
        when(this.repositoryMock.getWorkflow(anyString())).thenReturn(workflowMock);
        this.apiAdapter.executeWorkflow(REPOSITORY_NAME, workflowName, new WorkflowOptions());
        verify(workflowMock, times(1)).dispatch(defaultBranch, Map.of());
    }

    private GHWorkflowRun mockWorkflowRun() throws IOException, URISyntaxException {
        final GHWorkflowRun run = mock(GHWorkflowRun.class);
        when(run.getHtmlUrl()).thenReturn(new URI("http://of-workflow-run").toURL());
        when(run.getConclusion()).thenReturn(Conclusion.SUCCESS);
        return run;
    }

    @SuppressWarnings("unchecked")
    private GHWorkflow mockWorkflow(final GHWorkflowRun run) {
        final PagedIterator<GHWorkflowRun> ptor = mock(PagedIterator.class);
        when(ptor.hasNext()).thenReturn(run != null);
        if (run != null) {
            when(ptor.next()).thenReturn(run);
        }

        final PagedIterable<GHWorkflowRun> pable = mock(PagedIterable.class);
        when(pable.iterator()).thenReturn(ptor);

        final GHWorkflow workflow = mock(GHWorkflow.class);
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
    @SuppressWarnings("unchecked")
    void getClosedTickets() throws IOException, GitHubException {
        final GHIssue issue = mock(GHIssue.class);
        when(issue.getNumber()).thenReturn(123);
        final GHIssue pullRequest = mock(GHIssue.class);
        when(pullRequest.isPullRequest()).thenReturn(true);
        final GHIssueQueryBuilder.ForRepository query = mock(GHIssueQueryBuilder.ForRepository.class);
        final PagedIterable<GHIssue> issues = mock(PagedIterable.class);
        when(this.repositoryMock.queryIssues()).thenReturn(query);
        when(query.state(GHIssueState.CLOSED)).thenReturn(query);
        when(query.list()).thenReturn(issues);
        when(issues.toList()).thenReturn(List.of(issue, pullRequest));
        assertThat(this.apiAdapter.getClosedTickets(REPOSITORY_NAME), equalTo(Set.of(123)));
    }

    @ParameterizedTest
    @CsvSource({ "Not Found, E-RD-GH-1", "Bad credentials, E-RD-GH-13", "Unexpected response, E-RD-GH-14" })
    void wrapGitHubException(final String originalMessage, final String errorCode) throws IOException {
        reset(this.gitHubMock);
        final IOException cause = new IOException(originalMessage);
        doThrow(cause).when(this.gitHubMock).getRepository(REPOSITORY_NAME);
        final GitHubException exception = assertThrows(GitHubException.class,
                () -> this.apiAdapter.getRepositoryPrimaryLanguage(REPOSITORY_NAME));
        assertAll(() -> assertThat(exception.getMessage(), containsString(errorCode)),
                () -> assertThat(exception.getCause(), is(cause)));
    }

    @Test
    void testDownloadArtifactAsString() throws IOException, GitHubException {
        final long artifactId = 123;
        final GHArtifact artifactMock = mock(GHArtifact.class);
        when(this.gitHubMock.getRepository(REPOSITORY_NAME)).thenReturn(repositoryMock);
        when(repositoryMock.getArtifact(artifactId)).thenReturn(artifactMock);
        when(artifactMock.download(any())).thenReturn("hashsum file.jar");
        assertThat(this.apiAdapter.downloadArtifactAsString(REPOSITORY_NAME, artifactId), equalTo("hashsum file.jar"));
    }

    @Test
    void releaseCreateRelease() throws GitHubException, IOException, URISyntaxException {
        final String version = "4.5.6";
        final GitHubRelease release = releaseBuilder(version).build();
        final URL expectedHtmlUrl = mockGHRepository();
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

    @Test
    void additionalTags() throws GitHubException, IOException, URISyntaxException {
        final String v1 = "v1.2.3";
        final String v2 = "subfolder/v1.2.3";
        final GitHubRelease release = releaseBuilder("1.2.3") //
                .addTag(v1) //
                .addTag(v2) //
                .build();

        mockGHRepository();

        final Progress progress = mock(Progress.class);
        mockCommits(this.repositoryMock, true);
        this.apiAdapter.createGithubRelease(release, progress);
        verify(this.repositoryMock).createRef(eq("refs/tags/" + v1), any());
        verify(this.repositoryMock).createRef(eq("refs/tags/" + v2), any());
    }

    @SuppressWarnings("unchecked")
    static void mockCommits(final GHRepository repositoryMock, final boolean hasCommits) throws IOException {
        final GHRef ref = mock(GHRef.class);
        final GHRef.GHObject ro = mock(GHRef.GHObject.class);
        when(ref.getObject()).thenReturn(ro);
        when(repositoryMock.getRef(any())).thenReturn(ref);

        final GHCommitQueryBuilder builder = mock(GHCommitQueryBuilder.class);
        when(repositoryMock.queryCommits()).thenReturn(builder);

        when(builder.from(any())).thenReturn(builder);
        when(builder.pageSize(anyInt())).thenReturn(builder);

        final PagedIterable<GHCommit> iterable = mock(PagedIterable.class);
        when(builder.list()).thenReturn(iterable);

        final PagedIterator<GHCommit> iterator = mock(PagedIterator.class);
        when(iterable.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(hasCommits);
        if (hasCommits) {
            final GHCommit commit = mock(GHCommit.class);
            when(commit.getSHA1()).thenReturn("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            when(iterator.next()).thenReturn(commit);
        }
    }

    private GitHubRelease.Builder releaseBuilder(final String version) {
        return GitHubRelease.builder() //
                .repositoryName(REPOSITORY_NAME) //
                .version(version).header("title") //
                .releaseLetter("release letter");
    }

    private URL mockGHRepository() throws IOException, URISyntaxException {
        final URL htmlUrl = new URI("https://github.com/" + REPOSITORY_NAME + "/releases/releases/edit/untagged-123").toURL();
        final GHRelease releaseMock = mock(GHRelease.class);
        when(releaseMock.getHtmlUrl()).thenReturn(htmlUrl);
        when(releaseMock.isDraft()).thenReturn(true);
        final GHReleaseBuilder builder = releaseBuilderMock(releaseMock);
        when(this.repositoryMock.createRelease(any())).thenReturn(builder);
        when(this.gitHubMock.getRepository(REPOSITORY_NAME)).thenReturn(this.repositoryMock);
        return htmlUrl;
    }

    private GHReleaseBuilder releaseBuilderMock(final GHRelease release) throws IOException {
        final GHReleaseBuilder builder = mock(GHReleaseBuilder.class);
        when(builder.draft(anyBoolean())).thenReturn(builder);
        when(builder.body(any())).thenReturn(builder);
        when(builder.name(any())).thenReturn(builder);
        when(builder.create()).thenReturn(release);
        return builder;
    }
}
