package com.exasol.releasedroid.usecases.repository;

import java.util.Optional;

import com.exasol.releasedroid.usecases.repository.version.Version;

/**
 * Gate to access a repository content.
 */
public interface RepositoryGate {
    /**
     * Get the content of a file in this repository.
     *
     * @param filePath path of the file as a string
     * @return content as a string
     */
    public String getSingleFileContentAsString(final String filePath);

    /**
     * Check if a file exists.
     *
     * @param filePath file path
     * @return true if a file exists
     */
    public boolean hasFile(final String filePath);

    /**
     * Update a single file with a single commit.
     *
     * @param filePath      path to the file
     * @param newContent    new file content as a string
     * @param commitMessage commit message
     */
    public void updateFileContent(final String filePath, final String newContent, final String commitMessage);

    /**
     * Check if the content belongs to the default branch.
     *
     * @return true if the content belongs to the default branch
     */
    public boolean isOnDefaultBranch();

    /**
     * Get the branch name.
     *
     * @return branch name as a string
     */
    public String getBranchName();

    /**
     * Get the latest tag if exists.
     *
     * @return latest tag as a string or empty optional
     */
    public Optional<Version> getLatestTag();

    /**
     * Get repository's full name.
     *
     * @return full name as a String
     */
    public String getName();
}