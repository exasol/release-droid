package com.exasol.releasedroid.usecases.release;

import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * Prepares the repository for a release.
 */
public interface ReleaseManager {
    /**
     * Do a pre-release preparations.
     *
     * @param repository repository to prepare
     */
    public void prepareForRelease(Repository repository);

    /**
     * Do a post-release clean-up.
     *
     * @param repository repository to clean-up
     */
    public void cleanUpAfterRelease(Repository repository);
}