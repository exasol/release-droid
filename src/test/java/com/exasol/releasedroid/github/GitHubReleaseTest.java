package com.exasol.releasedroid.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GitHubReleaseTest {
    @Test
    void testValidGitHubRelease() {
        final GitHubRelease release = GitHubRelease.builder().repositoryName("repo").version("1.0.0").header("header")
                .releaseLetter("release letter").defaultBranchName("main").build();
        assertAll(() -> assertThat(release.getVersion(), equalTo("1.0.0")),
                () -> assertThat(release.getHeader(), equalTo("header")),
                () -> assertThat(release.getReleaseLetter(), equalTo("release letter")));
    }

    @Test
    void testGitHubReleaseEmptyRepositoryName() {
        final GitHubRelease.Builder builder = GitHubRelease.builder();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("'repositoryName' field is null or empty"));
    }

    @Test
    void testGitHubReleaseEmptyVersion() {
        final GitHubRelease.Builder builder = GitHubRelease.builder().repositoryName("repo");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("'version' field is null or empty"));
    }

    @Test
    void testGitHubReleaseEmptyHeader() {
        final GitHubRelease.Builder builder = GitHubRelease.builder().repositoryName("repo").version("1.0.0")
                .header("");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("'header' field is null or empty"));
    }

    @Test
    void testGitHubReleaseEmptyDefaultBranchName() {
        final GitHubRelease.Builder builder = GitHubRelease.builder().repositoryName("repo").version("1.0.0")
                .header("header");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("'defaultBranchName' field is null or empty"));
    }
}