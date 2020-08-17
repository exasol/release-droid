package com.exasol.release;

import com.exasol.ReleasePlatform;
import com.exasol.github.GitHubRepository;

/**
 * This factory class is responsible for instantiation of {@link ReleaseMaker}.
 */
public final class ReleaseMakerFactory {
    private ReleaseMakerFactory() {
        // prevent instantiation
    }

    /**
     * Create a new instance of {@link ReleaseMaker} depending on {@link ReleasePlatform}.
     * 
     * @param repository {@link GitHubRepository} with the project to release
     * @param platform release platform
     * @return a new instance of {@link ReleaseMaker}
     */
    public static ReleaseMaker createReleaseMaker(GitHubRepository repository, ReleasePlatform platform) {
        if (platform == ReleasePlatform.GITHUB) {
            return new GitHubReleaseMaker(repository);
        } else {
            throw new IllegalArgumentException("Release for platform " + platform + " is not supported");
        }
    }
}