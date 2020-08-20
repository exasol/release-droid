package com.exasol;

import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import com.exasol.release.ReleaseMaker;
import com.exasol.release.ReleaseMakerFactory;
import com.exasol.validation.*;

/**
 * This class provides high-level control over a repository.
 */
public class RepositoryHandler {
    private static final Logger LOGGER = Logger.getLogger(RepositoryHandler.class.getName());
    private final Set<ReleasePlatform> platforms;
    private final GitRepository repository;

    /**
     * Create a new instance of {@link RepositoryHandler}.
     * 
     * @param repository project's repository
     * @param platforms one or more {@link ReleasePlatform}
     */
    public RepositoryHandler(final GitRepository repository, final Set<ReleasePlatform> platforms) {
        this.repository = repository;
        this.platforms = platforms;
    }

    /**
     * Validate if the git project is ready for a release on specified platforms.
     * 
     * @param branch name of a branch to validate on
     */
    public void validate(final Optional<String> branch) {
        LOGGER.info(() -> "Validation started.");
        final GitRepositoryValidator validator = new GitRepositoryValidator(this.repository);
        validator.validate(branch);
        for (final ReleasePlatform platform : this.platforms) {
            final PlatformValidator platformValidator = PlatformValidatorFactory.createProjectValidator(this.repository,
                    platform);
            platformValidator.validate();
        }
        LOGGER.info(() -> "Validation completed successfully.");
    }

    /**
     * Release the project.
     */
    public void release() {
        LOGGER.info(() -> "Release started.");
        for (final ReleasePlatform platform : this.platforms) {
            final ReleaseMaker releaseMaker = ReleaseMakerFactory.createReleaseMaker(this.repository, platform);
            releaseMaker.makeRelease();
        }
        LOGGER.info(() -> "Release completed successfully.");
    }
}