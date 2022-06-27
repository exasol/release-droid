package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;

@ExtendWith(MockitoExtension.class)
class GitHubReleaseMakerTest {

    private static final String RELEASE_LETTER_BODY = "release letter body";
    private static final String RELEASE_LETTER_HEADER = "release letter header";
    private static final String REPO_NAME = "repo name";
    private static final String VERSION = "version";
    @Mock
    private GitHubGateway githubGatewayMock;
    @Mock
    private Repository repoMock;
    private GitHubReleaseMaker releaseMaker;

    @BeforeEach
    void setUp() {
        this.releaseMaker = new GitHubReleaseMaker(this.githubGatewayMock);
        when(this.repoMock.getName()).thenReturn(REPO_NAME);
        when(this.repoMock.getVersion()).thenReturn(VERSION);
        when(this.repoMock.getReleaseLetter(VERSION)).thenReturn(
                ReleaseLetter.builder("filename").header(RELEASE_LETTER_HEADER).body(RELEASE_LETTER_BODY).build());
    }

    @Test
    void makeRelease() throws GitHubException {
        this.releaseMaker.makeRelease(this.repoMock);
        final ArgumentCaptor<GitHubRelease> arg = ArgumentCaptor.forClass(GitHubRelease.class);
        verify(this.githubGatewayMock).createGithubRelease(arg.capture());
        final GitHubRelease actualRelease = arg.getValue();
        assertThat(actualRelease.getReleaseLetter(), equalTo(RELEASE_LETTER_BODY));
        assertThat(actualRelease.getVersion(), equalTo(VERSION));
        assertThat(actualRelease.getHeader(), equalTo(VERSION + ": " + RELEASE_LETTER_HEADER));
        assertThat(actualRelease.getRepositoryName(), equalTo(REPO_NAME));
        assertThat(actualRelease.hasUploadAssets(), is(true));
    }

    @Test
    void makeReleaseWithoutAssets() throws GitHubException {
        when(this.repoMock
                .getSingleFileContentAsString(".github/workflows/release_droid_upload_github_release_assets.yml"))
                        .thenThrow(new RepositoryException("expected"));
        this.releaseMaker.makeRelease(this.repoMock);
        final ArgumentCaptor<GitHubRelease> arg = ArgumentCaptor.forClass(GitHubRelease.class);
        verify(this.githubGatewayMock).createGithubRelease(arg.capture());
        final GitHubRelease actualRelease = arg.getValue();
        assertThat(actualRelease.hasUploadAssets(), is(false));
    }

    @Test
    void makeReleaseReturnsReleaseUrl() {
        final String releaseUrl = this.releaseMaker.makeRelease(this.repoMock);
        assertThat(releaseUrl, equalTo("https://github.com/" + REPO_NAME + "/releases/tag/" + VERSION));
    }
}
