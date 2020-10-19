package com.exasol.release.robot.repository;

import java.util.Optional;

/**
 * Represents a repository where a Git-based project is stored. A repository can be local or remote (GitHub, for
 * example).
 */
public interface GitRepository {
    /**
     * Get the latest tag if exists.
     *
     * @return latest tag as a string or empty optional
     */
    public Optional<String> getLatestTag();

    /**
     * Get a name of the default branch of the repository.
     * 
     * @return name of the default branch
     */
    public String getDefaultBranchName();

    /**
     * Get a new instance of {@link GitBranchContent} based on a user-specified branch.
     *
     * @param branchName name of a branch to get content from
     * @return new instance of {@link GitBranchContent}
     */
    // [impl->dsn~gr-retrieves-branch-content~1]
    public GitBranchContent getRepositoryContent(final String branchName);
}
