package com.exasol;

import java.util.Set;
import java.util.logging.Logger;

import com.exasol.release.ReleaseMaker;
import com.exasol.release.ReleaseMakerFactory;
import com.exasol.repository.GitRepository;
import com.exasol.repository.GitBranchContent;
import com.exasol.validation.*;

/**
 * This class provides high-level control over a repository.
 */
public class RepositoryHandler {
    private static final Logger LOGGER = Logger.getLogger(RepositoryHandler.class.getName());
    private final Set<Platform> platforms;
    private final GitRepository repository;

    /**
     * Create a new instance of {@link RepositoryHandler}.
     *
     * @param repository project's repository
     * @param platforms set of platforms to work with
     */
    public RepositoryHandler(final GitRepository repository, final Set<Platform> platforms) {
        this.repository = repository;
        this.platforms = platforms;
    }

    /**
     * Validate if the git project is ready for a release on specified platforms.
     */
    public void validate() {
        validate(this.repository.getDefaultBranchName());
    }

    /**
     * Validate if the git project is ready for a release on specified platforms.
     * 
     * @param branch name of a branch to validate on
     */
    public void validate(final String branch) {
        LOGGER.info(() -> "Validation started.");
        final GitRepositoryValidator validator = new GitRepositoryValidator(this.repository);
        validator.validate(branch);
        validatePlatforms(branch);
    }

    private void validatePlatforms(final String branch) {
        final GitBranchContent content = this.repository.getRepositoryContent(branch);
        for (final Platform platform : this.platforms) {
            final PlatformValidator platformValidator = PlatformValidatorFactory.createPlatformValidator(content,
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
        final GitBranchContent content = this.repository
                .getRepositoryContent(this.repository.getDefaultBranchName());
        for (final Platform platform : this.platforms) {
            final ReleaseMaker releaseMaker = ReleaseMakerFactory.createReleaseMaker(content, platform);
            releaseMaker.makeRelease();
        }
        LOGGER.info(() -> "Release completed successfully.");
    }
}