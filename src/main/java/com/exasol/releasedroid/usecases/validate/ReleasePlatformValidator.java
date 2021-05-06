package com.exasol.releasedroid.usecases.validate;

import com.exasol.releasedroid.usecases.report.Report;

/**
 * This interface provides platform-related validations.
 */
public interface ReleasePlatformValidator {
    /**
     * Validate if the project is ready for a release on a platform.
     *
     * @return validation report
     */
    public Report validate();
}