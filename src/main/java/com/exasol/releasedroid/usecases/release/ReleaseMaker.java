package com.exasol.releasedroid.usecases.release;

import com.exasol.releasedroid.usecases.ReleaseException;
import com.exasol.releasedroid.usecases.Repository;

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