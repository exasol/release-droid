package com.exasol.releasedroid.usecases.validate;

import com.exasol.releasedroid.usecases.Repository;
import com.exasol.releasedroid.usecases.report.Report;

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