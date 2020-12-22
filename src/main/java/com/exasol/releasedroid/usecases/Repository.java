package com.exasol.releasedroid.usecases;

import java.util.Map;
import java.util.Optional;

import com.exasol.releasedroid.repository.MavenPom;
import com.exasol.releasedroid.repository.ReleaseLetter;

/**
 * This class represents a repository content based on the latest commit of the user-specified branch.
 */
public interface Repository {
    /**
     * Get the content of a file in this repository.
     *
     * @param filePath path of the file as a string
     * @return content as a string
     */
    public String getSingleFileContentAsString(final String filePath);

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
    // [impl->dsn~repository-provides-current-version~1]
    public String getVersion();

    /**
     * Get key-value pairs for deliverable names and corresponding deliverable pathes.
     *
     * @return map with deliverables information
     */
    // [impl->dsn~repository-provides-deliverables-information~1]
    public Map<String, String> getDeliverables();

    /**
     * Get the latest tag if exists.
     *
     * @return latest tag as a string or empty optional
     */
    public Optional<String> getLatestTag();

    /**
     * Fet repository's full name.
     * 
     * @return full name as a String
     */
    public String getName();

    /**
     * Get a parsed Maven pom file.
     *
     * @return instance of {@link MavenPom}
     */
    public MavenPom getMavenPom();
}