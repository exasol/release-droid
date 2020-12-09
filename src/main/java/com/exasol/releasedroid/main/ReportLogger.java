package com.exasol.releasedroid.main;

import java.util.logging.Logger;

import com.exasol.releasedroid.report.ReportFormatterImpl;
import com.exasol.releasedroid.usecases.report.Report;

/**
 * This class logs report results.
 */
public class ReportLogger {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final Logger LOGGER = Logger.getLogger(ReportLogger.class.getName());
    private static ReportFormatterImpl reportFormatter = new ReportFormatterImpl();

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
            LOGGER.severe(() -> reportFormatter.formatReportWithFailuresOnly(report));
        } else {
            LOGGER.info(() -> ANSI_GREEN + reportFormatter.formatHeader(report) + ANSI_RESET);
        }
    }
}
