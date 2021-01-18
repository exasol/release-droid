package com.exasol.releasedroid.usecases.validate;

import com.exasol.releasedroid.usecases.report.Report;

/**
 * This interface provides repository-related validations.
 */
public interface RepositoryValidator {
    /**
     * Validate content of a repository.
     *
     * @return validation report
     */
    public Report validate();
}