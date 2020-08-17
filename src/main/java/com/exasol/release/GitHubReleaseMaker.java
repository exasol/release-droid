package com.exasol.release;

import java.util.logging.Logger;

import com.exasol.github.GitHubRepository;

/**
 * This class responds for releases on GitHub
 */
public class GitHubReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(GitHubReleaseMaker.class.getName());
    private final GitHubRepository repository;

    /**
     * Create a new {@link GitHubReleaseMaker}.
     * 
     * @param repository an instance of {@link GitHubRepository}
     */
    public GitHubReleaseMaker(final GitHubRepository repository) {
        this.repository = repository;
    }

    @Override
    public void makeRelease() {
        LOGGER.fine("Releasing on GitHub.");
        final String version = this.repository.getVersion();
        final String changes = this.repository.getChangesFile(version);
        final int firstLineEnd = changes.indexOf('\n');
        final String releaseName = changes.substring(0, firstLineEnd);
        final String releaseLetter = changes.substring(firstLineEnd + 1);
        this.repository.release(version, releaseName, releaseLetter);
    }
}