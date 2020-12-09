package com.exasol.releasedroid.usecases.logging;

import com.exasol.releasedroid.usecases.report.Report;

/**
 * Logger of reports.
 */
public interface ReportLogger {
    /**
     * Log report results.
     *
     * @param report report to log
     */
    public void logResults(final Report report);
}
