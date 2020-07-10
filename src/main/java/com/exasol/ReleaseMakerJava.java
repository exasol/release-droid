package com.exasol;

import com.exasol.validation.GitHubValidator;

public class ReleaseMakerJava implements ReleaseMaker {
    private static final String REPOSITORY_OWNER = "exasol";

    @Override
    public void validate(final ReleasePlatform platform, final String projectName) {
        switch (platform) {
        case GIT_HUB:
            validateGitHubProject(projectName);
        default:
            throw new UnsupportedOperationException("Java release on this platform is currently unsupported.");
        }
    }

    private void validateGitHubProject(final String projectName) {
        final GitHubValidator gitHubValidator = new GitHubValidator();
        gitHubValidator.validateReadyForRelease(REPOSITORY_OWNER, projectName);
    }

    @Override
    public void release(final ReleasePlatform platform, final String projectName) {

    }
}
