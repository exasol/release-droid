package com.exasol.validation;

import static com.exasol.ReleasePlatform.GITHUB;
import static com.exasol.ReleasePlatform.MAVEN_CENTRAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProjectValidatorFactoryTest {
    @Test
    void testCreateProjectValidatorGitHub() {
        assertThat(ProjectValidatorFactory.createProjectValidator(null, GITHUB),
                instanceOf(GitHubProjectValidator.class));
    }

    @Test
    void testCreateProjectValidatorInvalidPlatform() {
        assertThrows(IllegalArgumentException.class,
                () -> ProjectValidatorFactory.createProjectValidator(null, MAVEN_CENTRAL));
    }
}