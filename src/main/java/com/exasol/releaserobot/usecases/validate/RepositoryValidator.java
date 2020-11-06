package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.usecases.Report;
import com.exasol.releaserobot.usecases.Repository;

/**
 * This interface provides repository-related validations.
 */
public interface RepositoryValidator {
    /**
     * Validate content of a repository.
     *
     * @param repository repository to validate
     * @return validation report
     */
    public Report validate(final Repository repository);
}