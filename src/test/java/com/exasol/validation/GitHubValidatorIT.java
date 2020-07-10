package com.exasol.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.egit.github.core.Repository;
import org.junit.jupiter.api.Test;

class GitHubValidatorIT {
    @Test
    void validateProjectExists() {
        final GitHubValidator validator = new GitHubValidator();
        final Repository repository = validator.getRepositoryIfExists("exasol", "release-robot");
        assertThat(repository.getGitUrl(), equalTo("git://github.com/exasol/release-robot.git"));
    }

    @Test
    void validateProjectExistsThrowsException() {
        final GitHubValidator validator = new GitHubValidator();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.getRepositoryIfExists("exasol", "fake-name"));
        assertThat(exception.getMessage(), containsString("Repository 'fake-name' not found"));
    }
}
