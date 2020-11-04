package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.usecases.Report;

/**
 * This interface provides repository-related validations.
 */
public interface RepositoryValidator {
    /**
     * Validate content of a Git-based repository on the default branch.
     *
     * @return validation report
     */
    public Report validateDefaultBranch();

    /**
     * Validate content of a Git-based repository.
     *
     * @param branchName name of a branch to validate on
     * @return validation report
     */
    public Report validateBranch(final String branchName);
}