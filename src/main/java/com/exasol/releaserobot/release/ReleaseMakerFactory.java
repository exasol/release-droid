package com.exasol.releaserobot.release;

import static com.exasol.releaserobot.Platform.PlatformName.GITHUB;
import static com.exasol.releaserobot.Platform.PlatformName.MAVEN;

import com.exasol.releaserobot.MavenPlatform;
import com.exasol.releaserobot.Platform;
import com.exasol.releaserobot.Platform.PlatformName;
import com.exasol.releaserobot.github.GitHubPlatform;
import com.exasol.releaserobot.report.ReleaseReport;
import com.exasol.releaserobot.repository.GitBranchContent;

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
        } else if (platformName == MAVEN) {
            return new MavenReleaseMaker((MavenPlatform) platform, releaseReport);
        } else {
            throw new UnsupportedOperationException("E-RR-REL-1: Release for platform " + platform
                    + " is not supported. Please choose one of: " + PlatformName.availablePlatformNames().toString());
        }
    }
}