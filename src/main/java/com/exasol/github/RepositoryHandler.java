package com.exasol.github;

import java.util.Set;
import java.util.logging.Logger;

import com.exasol.ReleasePlatform;
import com.exasol.release.ReleaseMaker;
import com.exasol.release.ReleaseMakerFactory;
import com.exasol.validation.ProjectValidator;
import com.exasol.validation.ProjectValidatorFactory;

/**
 * This class provides high-level control over a repository.
 */
public class RepositoryHandler {
    private static final Logger LOGGER = Logger.getLogger(RepositoryHandler.class.getName());
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
        for (final ReleasePlatform platform : this.platforms) {
            final ProjectValidator projectValidator = ProjectValidatorFactory.createProjectValidator(this.repository,
                    platform);
            projectValidator.validate();
        }
        LOGGER.info(() -> "Validation completed successfully.");
    }

    /**
     * Release the project.
     */
    public void release() {
        LOGGER.info("Release started.");
        for (final ReleasePlatform platform : this.platforms) {
            final ReleaseMaker releaseMaker = ReleaseMakerFactory.createReleaseMaker(this.repository, platform);
            releaseMaker.makeRelease();
        }
        LOGGER.info(() -> "Release completed successfully.");
    }
}
