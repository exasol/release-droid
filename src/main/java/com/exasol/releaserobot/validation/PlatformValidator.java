package com.exasol.releaserobot.validation;

/**
 * A common interface for classes performing validation depending on a platform.
 */
public interface PlatformValidator {
    /**
     * Validate a project.
     */
    void validate();
}