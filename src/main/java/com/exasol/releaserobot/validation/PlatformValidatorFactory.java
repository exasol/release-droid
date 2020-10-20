package com.exasol.releaserobot.validation;

import static com.exasol.releaserobot.Platform.PlatformName.GITHUB;

import com.exasol.releaserobot.Platform;
import com.exasol.releaserobot.Platform.PlatformName;
import com.exasol.releaserobot.github.GitHubPlatform;
import com.exasol.releaserobot.report.ValidationReport;
import com.exasol.releaserobot.repository.GitBranchContent;

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
     * @param platform          release platform details
     * @param validationReport  instance of {@link ValidationReport}
     * @return new instance of {@link PlatformValidator}
     */
    public static PlatformValidator createPlatformValidator(final GitBranchContent repositoryContent,
            final Platform platform, final ValidationReport validationReport) {
        final PlatformName releasePlatform = platform.getPlatformName();
        if (releasePlatform == GITHUB) {
            return new GitHubPlatformValidator(repositoryContent, (GitHubPlatform) platform, validationReport);
        } else {
            // TODO replace the hardcoded list with the list of {@link Platform}s
            throw new UnsupportedOperationException("E-RR-VAL-9: Validation for release platform " + releasePlatform
                    + " is not supported. Please choose one of: github");
        }
    }
}