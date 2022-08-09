package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
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
        final GHWorkflow workflowMock = mockWorkflow();
        when(this.repositoryMock.getDefaultBranch()).thenReturn(defaultBranch);
        when(this.repositoryMock.getWorkflow(anyString())).thenReturn(workflowMock);
        doThrow(IOException.class).when(workflowMock).dispatch(defaultBranch, Map.of());
        assertAll(
                () -> assertThrows(GitHubException.class,
                        () -> this.apiAdapter.executeWorkflow(REPOSITORY_NAME, workflowName)),
                () -> verify(workflowMock, times(1)).dispatch(defaultBranch, Map.of()));
    }

    @SuppressWarnings("unchecked")
    private GHWorkflow mockWorkflow() throws IOException {
        final Instant INSTANT = Instant.parse("2022-01-01T13:00:10Z");
        final Duration DURATION = Duration.ofMinutes(2).plusSeconds(3);

        final GHWorkflowRun run = Mockito.mock(GHWorkflowRun.class);
        doReturn(Date.from(INSTANT)).when(run).getCreatedAt();
        doReturn(Date.from(INSTANT.plus(DURATION))).when(run).getUpdatedAt();

        final PagedIterator<GHWorkflowRun> ptor = Mockito.mock(PagedIterator.class);
        when(ptor.hasNext()).thenReturn(true);
        when(ptor.next()).thenReturn(run);

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

        final GitHubReleaseInfo info = this.apiAdapter.createGithubRelease(release);
        assertAll(() -> assertThat(info.getHtmlUrl(), equalTo(expectedHtmlUrl)), //
                () -> assertThat(info.isDraft(), equalTo(true)), //
                () -> assertThat(info.getTagUrl(), equalTo(expectedTagUrl)));

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