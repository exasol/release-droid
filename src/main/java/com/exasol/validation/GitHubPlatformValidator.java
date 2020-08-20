package com.exasol.validation;

import java.util.logging.Logger;

import com.exasol.GitRepository;

/**
 * This class checks if the GitHub project repository is ready for a release.
 */
public class GitHubPlatformValidator implements PlatformValidator {
    private static final Logger LOGGER = Logger.getLogger(GitHubPlatformValidator.class.getName());
    private final GitRepository repository;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     *
     * @param repository repository to validate
     */
    public GitHubPlatformValidator(final GitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validate() {
        LOGGER.fine("Validating GitHub-specific requirements.");
        // TODO add GitHub-specific validations on a new branch (check tickets bounding)
        // https://github.com/exasol/release-robot/issues/11
    }
}