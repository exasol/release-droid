package com.exasol.releasedroid.main;

import com.exasol.releasedroid.usecases.Report;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class helps us to deal with logging.
 */
public class LoggingTool {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    private LoggingTool() {
        // prevent instantiation
    }

    /**
     * Log report in colors.
     *
     * @param logger instance of {@link Logger}
     * @param report report to log
     */
    public static void logResults(Logger logger, final Report report) {
        if (report.hasFailures()) {
            logRedMessage(logger, Level.SEVERE, report.getShortDescription() + " " + report.getFailuresReport());
        } else {
            logGreenMessage(logger, Level.INFO, report.getShortDescription());
        }
    }

    /**
     * Log a message in a red color.
     *
     * @param logger  instance of {@link Logger}
     * @param level   log level
     * @param message message to log
     */
    public static void logRedMessage(Logger logger, Level level, String message) {
        logger.log(level, () -> ANSI_RED + message + ANSI_RESET);
    }

    private static void logGreenMessage(Logger logger, Level level, String message) {
        logger.log(level, () -> ANSI_GREEN + message + ANSI_RESET);
    }
}