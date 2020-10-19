package com.exasol.releaserobot.report;

import static com.exasol.releaserobot.Platform.PlatformName;

/**
 * Represents a release report.
 */
// [impl->dsn~rr-creates-release-report~1]
public class ReleaseReport extends AbstractReport {
    /**
     * Register a new successful release.
     *
     * @param platformName release platform name
     */
    public synchronized void addSuccessfulRelease(final PlatformName platformName) {
        this.results.add(ReleaseResult.successfulRelease(platformName));
    }

    /**
     * Register a new failed release.
     *
     * @param platformName release platform name
     * @param cause        failure cause
     */
    public void addFailedRelease(final PlatformName platformName, final String cause) {
        this.results.add(ReleaseResult.failedRelease(platformName, cause));
        super.hasFailures = true;
    }

    @Override
    public String getShortDescription() {
        return super.getShortDescription("Release");
    }
}