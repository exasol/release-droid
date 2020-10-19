package com.exasol.release.robot.repository;

import java.util.Map;

/**
 * This class represents Git repository content based on the latest commit of the user-specified branch.
 */
public interface GitBranchContent {
    /**
     * Get the content of a file in this repository.
     *
     * @param filePath path of the file as a string
     * @return content as a string
     */
    String getSingleFileContentAsString(String filePath);

    /**
     * Check if the branch is the default branch.
     *
     * @return true if the branch is default
     */
    public boolean isDefaultBranch();

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
    public ReleaseLetter getReleaseLetter(final String version);

    /**
     * Get a current project version.
     *
     * @return version as a string
     */
    // [impl->dsn~gr-provides-current-version~1]
    public String getVersion();

    /**
     * Get key-value pairs for deliverable names and corresponding deliverable pathes.
     * 
     * @return map with deliverables information
     */
    // [impl->dsn~gr-provides-deliverables-information~1]
    public Map<String, String> getDeliverables();
}