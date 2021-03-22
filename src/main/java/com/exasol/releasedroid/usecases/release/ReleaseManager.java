package com.exasol.releasedroid.usecases.release;

import com.exasol.releasedroid.adapter.github.GitHubException;
import com.exasol.releasedroid.usecases.repository.Repository;

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
    public void prepareForRelease(Repository repository);

    /**
     * Do a post-release clean-up.
     *
     * @param repository repository to clean-up
     * @throws GitHubException if some problem occurs
     */
    public void cleanUpAfterRelease(Repository repository);
}