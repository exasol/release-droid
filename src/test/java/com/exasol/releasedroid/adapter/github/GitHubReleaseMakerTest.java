package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    private GitHubReleaseMaker releaseMaker;

    @BeforeEach
    void setUp() {
        this.releaseMaker = new GitHubReleaseMaker(this.githubGatewayMock);
    }

    private ReleaseLetter releaseLetter(String releaseLetterHeader) {
        return ReleaseLetter.builder("filename") //
                .header(releaseLetterHeader) //
                .body(RELEASE_LETTER_BODY) //
                .build();
    }

    private Repository repoMock(String releaseLetterHeader) {
        Repository repoMock = mock(Repository.class);
        when(repoMock.getName()).thenReturn(REPO_NAME);
        when(repoMock.getVersion()).thenReturn(VERSION);
        when(repoMock.getReleaseLetter(VERSION)).thenReturn(releaseLetter(releaseLetterHeader));
        return repoMock;
    }

    // Actually this test might be redundant, as validation phase already aborts release on empty header of
    // release letter.
    @Test
    void emptyHeader_ThrowsException() {
        Repository repoMock = mock(Repository.class);
        when(repoMock.getVersion()).thenReturn(VERSION);
        when(repoMock.getReleaseLetter(VERSION)).thenReturn(releaseLetter(""));
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> this.releaseMaker.makeRelease(repoMock));
        assertThat(exception.getMessage(), containsString("E-RD-GH-28: Release header must not be empty."));
    }

    @Test
    void makeRelease() throws GitHubException {
        this.releaseMaker.makeRelease(repoMock(RELEASE_LETTER_HEADER));
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
        Repository repoMock = mock(Repository.class);
        when(repoMock.getSingleFileContentAsString(".github/workflows/release_droid_upload_github_release_assets.yml"))
                .thenThrow(new RepositoryException("expected"));
        this.releaseMaker.makeRelease(repoMock);
        final ArgumentCaptor<GitHubRelease> arg = ArgumentCaptor.forClass(GitHubRelease.class);
        verify(this.githubGatewayMock).createGithubRelease(arg.capture());
        final GitHubRelease actualRelease = arg.getValue();
        assertThat(actualRelease.hasUploadAssets(), is(false));
    }

    @Test
    void makeReleaseReturnsReleaseUrl() {
        final String releaseUrl = this.releaseMaker.makeRelease(repoMock(RELEASE_LETTER_HEADER));
        assertThat(releaseUrl, equalTo("https://github.com/" + REPO_NAME + "/releases/tag/" + VERSION));
    }
}
