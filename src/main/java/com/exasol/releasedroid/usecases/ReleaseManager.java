package com.exasol.releasedroid.usecases;

import com.exasol.releasedroid.github.GitHubException;

/**
 * Prepares the repository for a release.
 */
public interface ReleaseManager {
    /**
     * Do a pre-release preparations.
     * 
     * @param repository repository to prepare
     * @throws GitHubException if some problem occurs
     */
    public void prepareForRelease(Repository repository) throws GitHubException;

    /**
     * Do a post-release clean-up.
     *
     * @param repository repository to clean-up
     * @throws GitHubException if some problem occurs
     */
    public void cleanUpAfterRelease(Repository repository) throws GitHubException;
}