package com.exasol.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

class GitHubReleaseTest {
    @Test
    void testValidGitHubRelease() {
        final GitHubRelease release = GitHubRelease.builder().version("1.0.0").header("header")
                .releaseLetter("release letter").assets(Map.of("name", "path")).build();
        assertAll(() -> assertThat(release.getVersion(), equalTo("1.0.0")),
                () -> assertThat(release.getHeader(), equalTo("header")),
                () -> assertThat(release.getReleaseLetter(), equalTo("release letter")),
                () -> assertThat(release.getAssets(), equalTo(Map.of("name", "path"))));
    }

    @Test
    void testGitHubReleaseEmptyVersion() {
        final GitHubRelease.Builder builder = GitHubRelease.builder();
        final GitHubException exception = assertThrows(GitHubException.class, builder::build);
        assertThat(exception.getMessage(), containsString("'version' field is null or empty"));
    }

    @Test
    void testGitHubReleaseEmptyHeader() {
        final GitHubRelease.Builder builder = GitHubRelease.builder().version("1.0.0").header("");
        final GitHubException exception = assertThrows(GitHubException.class, builder::build);
        assertThat(exception.getMessage(), containsString("'header' field is null or empty"));
    }

    @Test
    void testGitHubReleaseEmptyAssets() {
        final GitHubRelease.Builder builder = GitHubRelease.builder().version("1.0.0").header("header");
        final GitHubException exception = assertThrows(GitHubException.class, builder::build);
        assertThat(exception.getMessage(), containsString("'assets' field is null or empty"));
    }
}