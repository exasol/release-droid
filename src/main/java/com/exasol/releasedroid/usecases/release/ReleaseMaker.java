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
     * @param branch instance of {@link Repository}
     */
    public void makeRelease(final Repository branch) throws ReleaseException;
}