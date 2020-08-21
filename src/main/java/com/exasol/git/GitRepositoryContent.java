package com.exasol.git;

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
     * Get a changes file as a string.
     *
     * @param version version as a string
     * @return changes file as a string
     */
    public String getChangesFile(String version);

    /**
     * Get a current project version.
     *
     * @return version as a string
     */
    public String getVersion();
}