package com.exasol.platform;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class GitHubControllerIT {
    @Test
    void testValidateProjectExists() {
        assertDoesNotThrow(() -> GitHubRepository.getAnonymousGitHubRepository("exasol", "release-robot"));
    }
//
//    @Test
//    void testValidateProjectExistsThrowsException() {
//        final NullPointerException exception = assertThrows(NullPointerException.class,
//                () -> GitHubRepository.getAnonymousGitHubRepository("exasol", "fake-name"));
//        assertThat(exception.getMessage(), containsString("Repository 'fake-name' not found"));
//    }

    @Test
    void testGetSingleFileContentAsString() {
        final GitHubRepository repository = GitHubRepository.getAnonymousGitHubRepository("exasol", "release-robot");
        final String pom = repository.getSingleFileContentAsString("pom.xml");
        assertThat(pom, containsString("<artifactId>release-robot</artifactId>"));
    }

//    @Test
//    void testGetLatestReleaseVersion() {
//        final GitHubRepository repository = GitHubRepository.getImmutableGitHubRepository("exasol", "release-robot");
//        repository.getLatestReleaseVersion();
//        final GHRepository repository = this.repository.getRepository("exasol", "virtual-schemas");
//        final Optional<String> latestRelease = this.repository.getLatestReleaseVersion(repository);
//        assertThat(latestRelease.get(), equalTo("4.0.2"));
//    }
}