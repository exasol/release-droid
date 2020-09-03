package com.exasol.repository;

/**
 * This class represents Git repository content based on the latest commit of the user-specified branch.
 */
public interface GitRepositoryContent {
    /**
     * Get a changelog file as a string.
     *
     * @return changelog file as a string
     */
    public String getChangelogFile();

    /**
     * Get a changes file as an instance of {@link ReleaseLetter}.
     *
     * @param version version as a string
     * @return release changes file
     */
    public ReleaseLetter getReleaseLetter(String version);

    /**
     * Get a current project version.
     *
     * @return version as a string
     */
    public String getVersion();
}