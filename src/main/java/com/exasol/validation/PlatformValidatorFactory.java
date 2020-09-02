package com.exasol.validation;

import com.exasol.ReleasePlatform;
import com.exasol.git.GitRepositoryContent;

/**
 * Responsible for instantiation of {@link PlatformValidator}s.
 */
public final class PlatformValidatorFactory {
    private PlatformValidatorFactory() {
        // prevent instantiation
    }

    /**
     * Create a new instance of the {@link PlatformValidator} depending on the {@link ReleasePlatform}.
     *
     * @param repositoryContent {@link GitRepositoryContent} with the project's content
     * @param platform release platform
     * @return new instance of {@link PlatformValidator}
     */
    public static PlatformValidator createProjectValidator(final GitRepositoryContent repositoryContent,
            final ReleasePlatform platform) {
        if (platform == ReleasePlatform.GITHUB) {
            return new GitHubPlatformValidator(repositoryContent);
        } else {
            throw new IllegalArgumentException(
                    "Validation for release platform " + platform + " is not supported. Please choose one of: github");
        }
    }
}