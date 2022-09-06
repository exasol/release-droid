package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GitHubReleaseTest {
    private static final String VERSION = "1.0.0";

    @Test
    void testValidGitHubRelease() {
        final GitHubRelease release = validRelease().build();
        assertAll(() -> assertThat(release.getVersion(), equalTo(VERSION)),
                () -> assertThat(release.getHeader(), equalTo("header")),
                () -> assertThat(release.getRepositoryName(), equalTo("repo")),
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
        final GitHubRelease.Builder builder = GitHubRelease.builder().repositoryName("repo").version(VERSION)
                .header("");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), containsString("'header' field is null or empty"));
    }

    @Test
    void noAdditionalTags() {
        assertThat(validRelease().build().additionalTags(), empty());
    }

    @Test
    void additionalTags() {
        final String v1 = "v" + VERSION;
        final String v2 = "subfolder/v" + VERSION;
        final GitHubRelease release = validRelease().addTag(v1).addTag(v2).build();
        assertThat(release.additionalTags(), containsInAnyOrder(v1, v2));
    }

    private GitHubRelease.Builder validRelease() {
        return GitHubRelease.builder().repositoryName("repo").version(VERSION).header("header")
                .releaseLetter("release letter");
    }
}