package com.exasol;

/**
 * A common interface for executing release goals.
 */
public interface ReleaseMaker {
    /**
     * Validate if the project is ready for a release.
     */
    public void validate();

    /**
     * Release the project.
     */
    public void release();
}