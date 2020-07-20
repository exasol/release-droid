package com.exasol.github;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.release.ReleaseMaker;
import com.exasol.release.ReleasePlatform;
import com.exasol.validation.ProjectValidator;

/**
 * This class provides high-level control over a repository.
 */
public class RepositoryHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryHandler.class);
    private final Set<ReleasePlatform> platforms;
    protected final GitHubRepository repository;

    /**
     * Create a new instance of {@link RepositoryHandler}.
     * 
     * @param repository in instance of {@link AbstractGitHubRepository}
     * @param platforms one or more {@link ReleasePlatform}
     */
    public RepositoryHandler(final GitHubRepository repository, final Set<ReleasePlatform> platforms) {
        this.repository = repository;
        this.platforms = platforms;
    }

    /**
     * Validate if the project is ready for a release.
     */
    public void validate() {
        LOGGER.info("Validation started.");
        final ProjectValidator projectValidator = new ProjectValidator(this.repository);
        projectValidator.validatePlatformIndependent();
        for (final ReleasePlatform platform : this.platforms) {
            projectValidator.validatePlatform(platform);
        }
        LOGGER.info("Validation successfully finished.");
    }

    /**
     * Release the project.
     */
    public void release() {
        LOGGER.info("Release started.");
        final ReleaseMaker releaseMaker = new ReleaseMaker(this.repository);
        for (final ReleasePlatform platform : this.platforms) {
            releaseMaker.makeRelease(platform);
        }
        LOGGER.info("Release successfully finished.");
    }
}