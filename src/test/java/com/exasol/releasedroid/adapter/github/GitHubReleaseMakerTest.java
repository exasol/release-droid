package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

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
    private GitHubReleaseInfo githubReleaseInfo;
    private GitHubReleaseMaker releaseMaker;

    @BeforeEach
    void setUp() {
        this.releaseMaker = new GitHubReleaseMaker(this.githubGatewayMock);
    }

    private ReleaseLetter releaseLetter(final String releaseLetterHeader) {
        return ReleaseLetter.builder("filename") //
                .header(releaseLetterHeader) //
                .body(RELEASE_LETTER_BODY) //
                .build();
    }

    private Repository repoMock(final String releaseLetterHeader) {
        final Repository repoMock = mock(Repository.class);
        when(repoMock.getName()).thenReturn(REPO_NAME);
        when(repoMock.getVersion()).thenReturn(VERSION);
        when(repoMock.getGitTags()).thenReturn(List.of(VERSION));
        when(repoMock.getReleaseLetter(VERSION)).thenReturn(releaseLetter(releaseLetterHeader));
        return repoMock;
    }

    // Actually this test might be redundant, as validation phase already aborts release on empty header of
    // release letter.
    @Test
    void emptyHeader_ThrowsException() {
        final Repository repoMock = mock(Repository.class);
        when(repoMock.getVersion()).thenReturn(VERSION);
        when(repoMock.getReleaseLetter(VERSION)).thenReturn(releaseLetter(""));
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> this.releaseMaker.makeRelease(repoMock, null));
        assertThat(exception.getMessage(), containsString("E-RD-GH-28: Release header must not be empty."));
    }

    @Test
    void makeRelease() throws GitHubException {
        mockReleaseInfo(this.githubGatewayMock);
        this.releaseMaker.makeRelease(repoMock(RELEASE_LETTER_HEADER), null);
        final ArgumentCaptor<GitHubRelease> arg = ArgumentCaptor.forClass(GitHubRelease.class);
        verify(this.githubGatewayMock).createGithubRelease(arg.capture(), any());
        final GitHubRelease actualRelease = arg.getValue();
        assertAll(() -> assertThat(actualRelease.getReleaseLetter(), equalTo(RELEASE_LETTER_BODY)),
                () -> assertThat(actualRelease.getVersion(), equalTo(VERSION)),
                () -> assertThat(actualRelease.getHeader(), equalTo(VERSION + ": " + RELEASE_LETTER_HEADER)),
                () -> assertThat(actualRelease.getRepositoryName(), equalTo(REPO_NAME)),
                () -> assertThat(actualRelease.hasUploadAssets(), is(true)));
    }

    @Test
    void makeReleaseWithoutAssets() throws GitHubException {
        mockReleaseInfo(this.githubGatewayMock);
        final Repository repoMock = repoMock(RELEASE_LETTER_HEADER);
        when(repoMock.getSingleFileContentAsString(".github/workflows/release_droid_upload_github_release_assets.yml"))
                .thenThrow(new RepositoryException("expected"));
        this.releaseMaker.makeRelease(repoMock, null);
        final ArgumentCaptor<GitHubRelease> arg = ArgumentCaptor.forClass(GitHubRelease.class);
        verify(this.githubGatewayMock).createGithubRelease(arg.capture(), any());
        final GitHubRelease actualRelease = arg.getValue();
        assertThat(actualRelease.hasUploadAssets(), is(false));
    }

    @Test
    void makeReleaseReturnsReleaseUrl() throws GitHubException {
        mockReleaseInfo(this.githubGatewayMock);
        final String expected = GitHubReleaseInfo.getTagUrl(REPO_NAME, VERSION);
        when(this.githubReleaseInfo.getTagUrl()).thenReturn(expected);
        final String actual = this.releaseMaker.makeRelease(repoMock(RELEASE_LETTER_HEADER), null);
        assertThat(actual, equalTo(expected));
    }

    private void mockReleaseInfo(final GitHubGateway githubGatewayMock) throws GitHubException {
        when(githubGatewayMock.createGithubRelease(any(), any())).thenReturn(this.githubReleaseInfo);
    }
}
