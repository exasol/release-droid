package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.usecases.Report;

/**
 * A common interface for classes performing validation depending on a platform.
 */
public interface PlatformValidator {
    /**
     * Validate a project.
     *
     * @return validation report
     */
    public Report validate();
}