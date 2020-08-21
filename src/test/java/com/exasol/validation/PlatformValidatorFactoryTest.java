package com.exasol.validation;

import static com.exasol.ReleasePlatform.GITHUB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.jupiter.api.Test;

class PlatformValidatorFactoryTest {
    @Test
    void testCreateProjectValidatorGitHub() {
        assertThat(PlatformValidatorFactory.createProjectValidator(null, GITHUB),
                instanceOf(GitHubPlatformValidator.class));
    }
}