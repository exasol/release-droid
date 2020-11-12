package com.exasol.releaserobot.usecases.release;

import com.exasol.releaserobot.usecases.Repository;

/**
 * A common interface for classes performing releases on different release platforms.
 */
public interface ReleaseMaker {
    /**
     * Make a release.
     *
     * @param branch instance of {@link Repository}
     */
    public void makeRelease(final Repository branch);
}