package com.exasol.releasedroid.main;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.exasol.releasedroid.usecases.Report;

public class ReportLogger {

    private static final Logger LOGGER = Logger.getLogger(ReportLogger.class.getName());

    /**
     * Log report in colors.
     *
     * @param logger instance of {@link Logger}
     * @param report report to log
     */
    public static void logResults(final Logger logger, final Report report) {
        LOGGER.addHandler(handler);
        if (report.hasFailures()) {
            logRedMessage(logger, Level.SEVERE, report.getShortDescription() + " " + report.getFailuresReport());
        } else {
            logGreenMessage(logger, Level.INFO, report.getShortDescription());
        }
    }
}
