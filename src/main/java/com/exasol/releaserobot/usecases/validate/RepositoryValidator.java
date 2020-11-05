package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.repository.Repository;
import com.exasol.releaserobot.usecases.Report;

/**
 * This interface provides repository-related validations.
 */
public interface RepositoryValidator {

    /**
     * Validate content of a repository.
     *
     * @param branchName name of a branch to validate on
     * @return validation report
     */
    public Report validate(final Repository repository);
}