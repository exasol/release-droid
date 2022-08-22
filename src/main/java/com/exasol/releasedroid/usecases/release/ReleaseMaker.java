package com.exasol.releasedroid.usecases.release;

import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * A common interface for classes performing releases on different release platforms.
 */
public interface ReleaseMaker {

    // [impl->dsn~estimate-duration~1]
    /**
     * @param repository instance of {@link Repository}
     * @return {@link Estimation} for release duration
     */
    Estimation estimateDuration(final Repository repository);

    /**
     * Make a release.
     *
     * @param repository instance of {@link Repository}
     * @param progress   {@link Progress} to display progress based on a priori estimation
     * @return release output
     */
    String makeRelease(final Repository repository, Progress progress) throws ReleaseException;
}