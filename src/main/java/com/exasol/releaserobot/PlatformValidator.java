package com.exasol.releaserobot;

import com.exasol.releaserobot.report.ValidationReport;

/**
 * A common interface for classes performing validation depending on a platform.
 */
public interface PlatformValidator {
    /**
     * Validate a project.
     */
    public void validate(ValidationReport validationReport);
}