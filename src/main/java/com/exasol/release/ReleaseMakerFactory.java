package com.exasol.release;

import static com.exasol.Platform.PlatformName.GITHUB;

import com.exasol.Platform;
import com.exasol.Platform.PlatformName;
import com.exasol.github.GitHubPlatform;
import com.exasol.report.ReleaseReport;
import com.exasol.repository.GitBranchContent;

/**
 * This factory class is responsible for instantiation of {@link ReleaseMaker}.
 */
public final class ReleaseMakerFactory {
    private ReleaseMakerFactory() {
        // prevent instantiation
    }

    /**
     * Create a new instance of the {@link ReleaseMaker} depending on the {@link Platform}.
     * 
     * @param content       {@link GitBranchContent} with the project content to release
     * @param platform      release platform
     * @param releaseReport release report
     * @return new instance of {@link ReleaseMaker}
     */
    public static ReleaseMaker createReleaseMaker(final GitBranchContent content, final Platform platform,
            final ReleaseReport releaseReport) {
        final PlatformName platformName = platform.getPlatformName();
        if (platformName == GITHUB) {
            return new GitHubReleaseMaker(content, (GitHubPlatform) platform, releaseReport);
        } else {
            // TODO: remove hardcoded list of supported platforms
            throw new UnsupportedOperationException(
                    "E-RR-REL-1: Release for platform " + platform + " is not supported. Please choose one of: github");
        }
    }
}