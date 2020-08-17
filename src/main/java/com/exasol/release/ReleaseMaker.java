package com.exasol.release;

import com.exasol.ReleasePlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.github.GitHubRepository;

/**
 * This class handles a release on different platforms.
 */
public class ReleaseMaker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseMaker.class);
    private final GitHubRepository repository;

    /**
     * Create a new {@link ReleaseMaker}.
     * 
     * @param repository an instance of {@link GitHubRepository}
     */
    public ReleaseMaker(final GitHubRepository repository) {
        this.repository = repository;
    }

    /**
     * Make a release on a specified platform.
     * 
     * @param platform platform to release on
     */
    public void makeRelease(final ReleasePlatform platform) {
        if (platform == ReleasePlatform.GITHUB) {
            releaseGitHub();
        } else {
            throw new IllegalArgumentException("Release for platform " + platform + " is not supported");
        }
    }

    private void releaseGitHub() {
        LOGGER.debug("Releasing on GitHub.");
        final String changes = this.repository.getChangesFile();
        final int firstLineEnd = changes.indexOf('\n');
        final String releaseName = changes.substring(0, firstLineEnd);
        final String releaseLetter = changes.substring(firstLineEnd + 1);
        this.repository.release(releaseName, releaseLetter);
    }
}