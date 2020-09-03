package com.exasol.release;

import java.util.logging.Logger;

import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitRepositoryContent;
import com.exasol.repository.ReleaseLetter;

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
        final ReleaseLetter releaseLetter = this.content.getReleaseLetter(version);
        final String body = releaseLetter.getBody().orElse("");
        final String header = releaseLetter.getHeader().orElse(version);
        this.gitHubPlatform.release(version, header, body);
    }
}