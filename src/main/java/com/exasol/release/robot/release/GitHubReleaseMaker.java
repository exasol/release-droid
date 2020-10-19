package com.exasol.release.robot.release;

import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.release.robot.github.GitHubPlatform;
import com.exasol.release.robot.github.GitHubRelease;
import com.exasol.release.robot.report.ReleaseReport;
import com.exasol.release.robot.repository.GitBranchContent;
import com.exasol.release.robot.repository.ReleaseLetter;

/**
 * This class responds for releases on GitHub
 */
public class GitHubReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(GitHubReleaseMaker.class.getName());
    private final GitBranchContent content;
    private final GitHubPlatform gitHubPlatform;
    private final ReleaseReport releaseReport;

    /**
     * Create a new {@link GitHubReleaseMaker}.
     * 
     * @param content        repository content to release
     * @param gitHubPlatform instance of {@link GitHubPlatform}
     * @param releaseReport  release report
     */
    public GitHubReleaseMaker(final GitBranchContent content, final GitHubPlatform gitHubPlatform,
            final ReleaseReport releaseReport) {
        this.content = content;
        this.gitHubPlatform = gitHubPlatform;
        this.releaseReport = releaseReport;
    }

    @Override
    // [impl->dsn~create-new-github-release~1]
    // [impl->dsn~retrieve-github-release-header-from-release-letter~1]
    // [impl->dsn~retrieve-github-release-body-from-release-letter~1]
    public boolean makeRelease() {
        LOGGER.fine("Releasing on GitHub.");
        final String version = this.content.getVersion();
        final ReleaseLetter releaseLetter = this.content.getReleaseLetter(version);
        final String body = releaseLetter.getBody().orElse("");
        final String header = releaseLetter.getHeader().orElse(version);
        final GitHubRelease release = GitHubRelease.builder().version(version).header(header).releaseLetter(body)
                .assets(this.content.getDeliverables()).build();
        try {
            this.gitHubPlatform.release(release);
            this.releaseReport.addSuccessfulRelease(this.gitHubPlatform.getPlatformName());
            return true;
        } catch (final RuntimeException runtimeException) {
            this.releaseReport.addFailedRelease(this.gitHubPlatform.getPlatformName(),
                    ExceptionUtils.getStackTrace(runtimeException));
            return false;
        }
    }
}