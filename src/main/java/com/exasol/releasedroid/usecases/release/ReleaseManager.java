package com.exasol.releasedroid.usecases.release;

import java.nio.file.Path;

import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;
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
    void prepareForRelease(Repository repository);

    // [impl->dsn~estimate-duration~1]
    /**
     * Estimate overall duration including the estimations for all platforms and potential additional overhead created
     * by the release manager.
     *
     * @param repository          repository to estimate release duration for
     * @param platformEstimations aggregated estimation for releasing to the configured platforms for the current
     *                            release
     * @return started instance of {@link Progress} including the {@code platformEstimations} plus the estimation for
     *         the overhead activities of the {@link ReleaseManager} itself
     */
    Progress estimateDuration(final Repository repository, Estimation platformEstimations);

    /**
     * Do a post-release clean-up.
     *
     * @param repository repository to clean-up
     */
    void cleanUpAfterRelease(Repository repository);

    /**
     * Generate a release guide.
     *
     * @param repository   repository to retrieve additional information for release guide
     * @param gitHubTagUrl HTML URL of GitHub release
     * @param destination  path to release guide
     */
    void generateReleaseGuide(Repository repository, String gitHubTagUrl, Path destination);
}