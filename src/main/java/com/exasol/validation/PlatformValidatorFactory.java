package com.exasol.validation;

import com.exasol.GitRepository;
import com.exasol.ReleasePlatform;

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
     * @param repository {@link GitRepository} with the project to release
     * @param platform release platform
     * @return new instance of {@link PlatformValidator}
     */
    public static PlatformValidator createProjectValidator(final GitRepository repository,
            final ReleasePlatform platform) {
        if (platform == ReleasePlatform.GITHUB) {
            return new GitHubPlatformValidator(repository);
        } else {
            throw new IllegalArgumentException(
                    "Validation for release platform " + platform + " is not supported. Please choose one of: github");
        }
    }
}