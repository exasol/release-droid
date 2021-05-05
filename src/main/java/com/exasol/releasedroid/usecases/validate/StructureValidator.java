package com.exasol.releasedroid.usecases.validate;

import com.exasol.releasedroid.usecases.report.Report;

/**
 * This interface provides repository-related validations.
 */
public interface StructureValidator {
    /**
     * Validate structure of a repository.
     *
     * @return validation report
     */
    public Report validate();
}