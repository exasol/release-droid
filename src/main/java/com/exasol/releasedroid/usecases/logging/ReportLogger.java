package com.exasol.releasedroid.usecases.logging;

import java.util.logging.Logger;

import com.exasol.releasedroid.usecases.report.Report;

/**
 * Logger of reports.
 */
public class ReportLogger {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final Logger LOGGER = Logger.getLogger(ReportLogger.class.getName());
    private final ReportFormatter reportFormatter = new ReportFormatter();

    /**
     * Log report results.
     *
     * @param report report to log
     */
    public void logResults(final Report report) {
        if (report.hasFailures()) {
            LOGGER.severe(() -> this.reportFormatter.formatReportWithFailuresOnly(report));
        } else {
            LOGGER.info(() -> ANSI_GREEN + this.reportFormatter.formatHeader(report) + ANSI_RESET);
        }
    }
}
