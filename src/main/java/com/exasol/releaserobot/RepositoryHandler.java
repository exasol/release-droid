package com.exasol.releaserobot;

import java.util.Set;
import java.util.logging.Logger;

import com.exasol.releaserobot.report.ReleaseReport;
import com.exasol.releaserobot.report.ValidationReport;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.GitRepository;
import com.exasol.releaserobot.validation.*;

/**
 * This class provides high-level control over a repository.
 */
public class RepositoryHandler {
    private static final Logger LOGGER = Logger.getLogger(RepositoryHandler.class.getName());
    private final ValidationReport validationReport = new ValidationReport();
    private final ReleaseReport releaseReport = new ReleaseReport();
    private final Set<Platform> platforms;
    private final GitRepository repository;

    /**
     * Create a new instance of {@link RepositoryHandler}.
     *
     * @param repository project's repository
     * @param platforms  set of platforms to work with
     */
    public RepositoryHandler(final GitRepository repository, final Set<Platform> platforms) {
        this.repository = repository;
        this.platforms = platforms;
    }

    /**
     * Validate if the git project is ready for a release on specified platforms.
     * 
     * @return validation report
     */
    public ValidationReport validate() {
        return validate(this.repository.getDefaultBranchName());
    }

    /**
     * Validate if the git project is ready for a release on specified platforms.
     * 
     * @param branch name of a branch to validate on
     * @return validation report
     */
    public ValidationReport validate(final String branch) {
        LOGGER.info(() -> "Validation started.");
        final GitRepositoryValidator validator = new GitRepositoryValidator(this.repository, this.validationReport);
        validator.validate(branch);
        validatePlatforms(branch);
        return this.validationReport;
    }

    private void validatePlatforms(final String branch) {
        final GitBranchContent content = this.repository.getRepositoryContent(branch);
        for (final Platform platform : this.platforms) {
            final PlatformValidator platformValidator = PlatformValidatorFactory.createPlatformValidator(content,
                    platform, this.validationReport);
            platformValidator.validate();
        }
    }

}