package com.exasol.releasedroid.usecases.release;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * A common interface for classes performing releases on different release platforms.
 */
public interface ReleaseMaker {
    /**
     * Make a release.
     *
     * @param repository instance of {@link Repository}
     * @return release output
     */
    public String makeRelease(final Repository repository) throws ReleaseException;
}