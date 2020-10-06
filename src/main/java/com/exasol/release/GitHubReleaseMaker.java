package com.exasol.release;

import java.util.logging.Logger;

import com.exasol.github.GitHubPlatform;
import com.exasol.github.GitHubRelease;
import com.exasol.repository.GitBranchContent;
import com.exasol.repository.ReleaseLetter;

/**
 * This class responds for releases on GitHub
 */
public class GitHubReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(GitHubReleaseMaker.class.getName());
    private final GitBranchContent content;
    private final GitHubPlatform gitHubPlatform;

    /**
     * Create a new {@link GitHubReleaseMaker}.
     *
     * @param content repository content to release
     * @param gitHubPlatform instance of {@link GitHubPlatform}
     */
    public GitHubReleaseMaker(final GitBranchContent content, final GitHubPlatform gitHubPlatform) {
        this.content = content;
        this.gitHubPlatform = gitHubPlatform;
    }

    @Override
    // [impl->dsn~create-new-github-release~1]
    // [impl->dsn~retrieve-github-release-header-from-release-letter~1]
    // [impl->dsn~retrieve-github-release-body-from-release-letter~1]
    public void makeRelease() {
        LOGGER.fine("Releasing on GitHub.");
        final String version = this.content.getVersion();
        final ReleaseLetter releaseLetter = this.content.getReleaseLetter(version);
        final String body = releaseLetter.getBody().orElse("");
        final String header = releaseLetter.getHeader().orElse(version);
        final GitHubRelease release = GitHubRelease.builder().version(version).header(header).releaseLetter(body)
                .assets(this.content.getDeliverables()).build();
        this.gitHubPlatform.release(release);
    }
}