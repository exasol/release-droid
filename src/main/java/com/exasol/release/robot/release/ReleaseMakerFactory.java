package com.exasol.release.robot.release;

import static com.exasol.release.robot.Platform.PlatformName.GITHUB;

import com.exasol.release.robot.Platform;
import com.exasol.release.robot.Platform.PlatformName;
import com.exasol.release.robot.github.GitHubPlatform;
import com.exasol.release.robot.report.ReleaseReport;
import com.exasol.release.robot.repository.GitBranchContent;

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