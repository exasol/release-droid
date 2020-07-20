package com.exasol.release;

import com.exasol.github.GitHubRepository;

/**
 * This class handles a release on different platforms.
 */
public class ReleaseMaker {
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
        final String changes = repository.getChangesFile();
        final String version = repository.getVersion();
        final int firstLineEnd = changes.indexOf('\n');
        this.repository.release(version, changes.substring(0, firstLineEnd), changes.substring(firstLineEnd + 1));
    }
}