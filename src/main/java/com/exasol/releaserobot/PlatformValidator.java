package com.exasol.releaserobot;

import java.util.List;

import com.exasol.releaserobot.report.ValidationResult;

/**
 * A common interface for classes performing validation depending on a platform.
 */
public interface PlatformValidator {
    /**
     * Validate a project.
     * 
     * @return list of validation results
     */
    public List<ValidationResult> validate();
}