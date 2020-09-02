package com.exasol.release;

import java.util.logging.Logger;

import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitRepositoryContent;
import com.exasol.repository.ReleaseChangesLetter;

/**
 * This class responds for releases on GitHub
 */
public class GitHubReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(GitHubReleaseMaker.class.getName());
    private final GitRepositoryContent content;
    private final GitHubPlatform gitHubPlatform;

    /**
     * Create a new {@link GitHubReleaseMaker}.
     *
     * @param content repository content to release
     * @param gitHubPlatform instance of {@link GitHubPlatform}
     */
    public GitHubReleaseMaker(final GitRepositoryContent content, final GitHubPlatform gitHubPlatform) {
        this.content = content;
        this.gitHubPlatform = gitHubPlatform;
    }

    @Override
    public void makeRelease() {
        LOGGER.fine("Releasing on GitHub.");
        final String version = this.content.getVersion();
        final ReleaseChangesLetter changes = this.content.getReleaseChangesLetter(version);
        final String body = changes.getBody().orElse("");
        final String header = changes.getHeader().orElse(version);
        this.gitHubPlatform.release(version, header, body);
    }
}