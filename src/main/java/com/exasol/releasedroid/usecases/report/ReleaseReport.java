package com.exasol.releasedroid.usecases.report;

import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * Report for the release use case.
 */
public class ReleaseReport extends AbstractReport {
    private ReleaseReport(final String reportName, final PlatformName platformName) {
        super(reportName, platformName);
    }

    /**
     * Create a new instance of {@link ReleaseReport}.
     *
     * @param platformName platform name
     * @return new instance of {@link ReleaseReport}
     */
    public static ReleaseReport create(final PlatformName platformName) {
        return new ReleaseReport("Release", platformName);
    }

    /**
     * Create a new instance of {@link ReleaseReport}.
     *
     * @return new instance of {@link ReleaseReport}
     */
    public static ReleaseReport create() {
        return create(null);
    }
}