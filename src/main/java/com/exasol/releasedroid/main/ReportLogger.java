package com.exasol.releasedroid.main;

import com.exasol.releasedroid.usecases.Report;

import java.util.logging.Logger;

/**
 * This class logs report results.
 */
public class ReportLogger {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final Logger LOGGER = Logger.getLogger(ReportLogger.class.getName());

    private ReportLogger() {
        // prevent instantiation
    }

    /**
     * Log report results.
     *
     * @param report report to log
     */
    public static void logResults(final Report report) {
        if (report.hasFailures()) {
            LOGGER.severe(() -> report.getShortDescription() + " " + report.getFailuresReport());
        } else {
            LOGGER.info(() -> ANSI_GREEN + report.getShortDescription() + ANSI_RESET);
        }
    }
}
