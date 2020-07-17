package com.exasol.platform;

import java.util.Set;

import com.exasol.*;

/**
 * This class instantiates a corresponding {@link ReleaseMaker}.
 */
public class RepositoryHandlerFactory {
    private RepositoryHandlerFactory() {
        // prevent instantiation
    }

    /**
     * Instantiate a {@link RepositoryHandler}. Currently this method always creates an instance of
     * {@link JavaRepositoryHandler}.
     * 
     * @param repository instance of {@link GitHubRepository}
     * @param platformsList set of release platforms
     * @return a new instance of {@link RepositoryHandler}
     */
    public static RepositoryHandler getReleaseHandler(final GitHubRepository repository,
            final Set<ReleasePlatform> platformsList) {
        return new JavaRepositoryHandler(repository, platformsList);
    }
}