package com.exasol.validation;

import static com.exasol.Platform.PlatformName.GITHUB;

import com.exasol.Platform;
import com.exasol.Platform.PlatformName;
import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitBranchContent;

/**
 * Responsible for instantiation of {@link PlatformValidator}s.
 */
public final class PlatformValidatorFactory {
    private PlatformValidatorFactory() {
        // prevent instantiation
    }

    /**
     * Create a new instance of the {@link PlatformValidator} depending on the {@link PlatformName}.
     *
     * @param repositoryContent {@link GitBranchContent} with the project's content
     * @param platform release platform details
     * @return new instance of {@link PlatformValidator}
     */
    public static PlatformValidator createPlatformValidator(final GitBranchContent repositoryContent,
            final Platform platform) {
        final PlatformName releasePlatform = platform.getPlatformName();
        if (releasePlatform == GITHUB) {
            return new GitHubPlatformValidator(repositoryContent, (GitHubPlatform) platform);
        } else {
            throw new UnsupportedOperationException("Validation for release platform " + releasePlatform
                    + " is not supported. Please choose one of: github");
        }
    }
}