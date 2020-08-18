package com.exasol.validation;

import com.exasol.ReleasePlatform;
import com.exasol.github.GitHubRepository;

/**
 * This factory class is responsible for instantiation of {@link ProjectValidator}.
 */
public final class ProjectValidatorFactory {
    private ProjectValidatorFactory() {
        // prevent instantiation
    }

    /**
     * Create a new instance of {@link ProjectValidator} depending on {@link ReleasePlatform}.
     *
     * @param repository {@link GitHubRepository} with the project to release
     * @param platform release platform
     * @return a new instance of {@link ProjectValidator}
     */
    public static ProjectValidator createProjectValidator(final GitHubRepository repository,
            final ReleasePlatform platform) {
        if (platform == ReleasePlatform.GITHUB) {
            return new GitHubProjectValidator(repository);
        } else {
            throw new IllegalArgumentException("Validation for release platform " + platform + " is not supported. Please choose one of: github");
        }
    }
}
