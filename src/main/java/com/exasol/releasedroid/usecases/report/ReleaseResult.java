package com.exasol.releasedroid.usecases.report;

import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * Represents a result of a single release.
 */
public class ReleaseResult extends Result {
    private final PlatformName platformName;
    private final String failCause;

    private ReleaseResult(final PlatformName platformName, final boolean successful, final String failCause) {
        super(successful);
        this.platformName = platformName;
        this.failCause = failCause;
    }

    /**
     * Create a new successful release.
     *
     * @param platformName platform name
     * @return new instance of {@link ReleaseResult}
     */
    public static ReleaseResult successfulRelease(final PlatformName platformName) {
        return new ReleaseResult(platformName, true, null);
    }

    /**
     * Create a new failed release.
     *
     * @param platformName platform name
     * @param cause        failure cause
     * @return new instance of {@link ReleaseResult}
     */
    public static ReleaseResult failedRelease(final PlatformName platformName, final String cause) {
        return new ReleaseResult(platformName, false, cause);
    }

    @Override
    public String toString() {
        if (isSuccessful()) {
            return "Success. " + this.platformName;
        } else {
            return "Fail.    " + this.platformName + ": " + this.failCause;
        }
    }
}