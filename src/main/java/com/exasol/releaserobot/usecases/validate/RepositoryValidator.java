package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.repository.RepositoryTOGOAWAY;
import com.exasol.releaserobot.usecases.Report;

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
    public Report validate(final RepositoryTOGOAWAY repository);
}