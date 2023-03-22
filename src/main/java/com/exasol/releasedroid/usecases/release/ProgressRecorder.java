package com.exasol.releasedroid.usecases.release;

import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * This interface describes a recorder for incremental saving the current state or progress.
 *
 * <p>
 * In case of a failure of the current session this enables to recover and continue later on.
 */
public interface ProgressRecorder {

    /**
     * Incrementally save the current progress enabling to recover and continue after a failure of the current session.
     *
     * @param repositoryName repository name
     * @param releaseVersion release version
     * @param platformName   platform name
     * @param releaseOutput  output to save
     */
    public void saveProgress(final String repositoryName, final String releaseVersion, final PlatformName platformName,
            final String releaseOutput);
}
