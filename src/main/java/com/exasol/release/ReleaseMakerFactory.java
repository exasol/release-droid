package com.exasol.release;

import com.exasol.GitRepository;
import com.exasol.ReleasePlatform;

/**
 * This factory class is responsible for instantiation of {@link ReleaseMaker}.
 */
public final class ReleaseMakerFactory {
    private ReleaseMakerFactory() {
        // prevent instantiation
    }

    /**
     * Create a new instance of the {@link ReleaseMaker} depending on the {@link ReleasePlatform}.
     * 
     * @param repository {@link GitRepository} with the project to release
     * @param platform release platform
     * @return new instance of {@link ReleaseMaker}
     */
    public static ReleaseMaker createReleaseMaker(final GitRepository repository, final ReleasePlatform platform) {
        if (platform == ReleasePlatform.GITHUB) {
            return new GitHubReleaseMaker(repository);
        } else {
            throw new IllegalArgumentException(
                    "Release for platform " + platform + " is not supported. Please choose one of: github");
        }
    }
}