package com.exasol.releasedroid.usecases.validate;

import com.exasol.releasedroid.usecases.Report;
import com.exasol.releasedroid.usecases.Repository;

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