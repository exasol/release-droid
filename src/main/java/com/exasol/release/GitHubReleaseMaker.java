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
        final String releaseName = readReleaseNameFromReleaseLetter(changes);
        final String releaseLetter = readReleaseContentFromReleaseLetter(changes);
        this.repository.release(version, releaseName, releaseLetter);
    }

    private String readReleaseNameFromReleaseLetter(final String changes) {
        final int firstLineEnd = changes.indexOf('\n');
        final int endIndex = firstLineEnd >= 0 ? firstLineEnd : changes.length();
        return changes.substring(0, endIndex);
    }

    private String readReleaseContentFromReleaseLetter(final String changes) {
        final int firstLineEnd = changes.indexOf('\n');
        return firstLineEnd >= 0 ? changes.substring(firstLineEnd + 1) : "";
    }
}