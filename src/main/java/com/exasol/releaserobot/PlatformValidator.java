package com.exasol.releaserobot;

import com.exasol.releaserobot.report.Report;

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