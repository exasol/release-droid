package com.exasol;

public interface RepositoryHandler {
    /**
     * Validate if the project is ready for a release.
     */
    public void validate();

    /**
     * Release the project.
     */
    public void release();

    /**
     * Get a project version to be released.
     *
     * @return version as a string
     */
    public String getVersion();
}
