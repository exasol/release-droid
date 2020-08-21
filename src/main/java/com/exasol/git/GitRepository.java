package com.exasol.git;

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
     * Create a new release tag on the latest commit of the current branch.
     *
     * @param tag tag to release
     * @param releaseLetter release letter
     */
    public void release(String tag, String releaseLetter);

    /**
     * Get a new instance of {@link GitRepositoryContent} based on a user-specified branch.
     *
     * @param branchName name of a branch to get content from
     * @return new instance of {@link GitRepositoryContent}
     */
    public GitRepositoryContent getRepositoryContent(String branchName);
}