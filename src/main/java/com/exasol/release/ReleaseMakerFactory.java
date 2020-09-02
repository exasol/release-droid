package com.exasol.release;

import static com.exasol.Platform.PlatformName.GITHUB;

import com.exasol.Platform;
import com.exasol.Platform.PlatformName;
import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitRepositoryContent;

/**
 * This factory class is responsible for instantiation of {@link ReleaseMaker}.
 */
public final class ReleaseMakerFactory {
    private ReleaseMakerFactory() {
        // prevent instantiation
    }

    /**
     * Create a new instance of the {@link ReleaseMaker} depending on the {@link Platform}.
     * 
     * @param content {@link GitRepositoryContent} with the project content to release
     * @param platform release platform
     * @return new instance of {@link ReleaseMaker}
     */
    public static ReleaseMaker createReleaseMaker(final GitRepositoryContent content, final Platform platform) {
        final PlatformName platformName = platform.getPlatformName();
        if (platformName == GITHUB) {
            return new GitHubReleaseMaker(content, (GitHubPlatform) platform);
        } else {
            throw new UnsupportedOperationException(
                    "Release for platform " + platform + " is not supported. Please choose one of: github");
        }
    }
}