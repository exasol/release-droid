package com.exasol.releasedroid.main;

import java.util.logging.*;

import com.exasol.releasedroid.usecases.Report;

/**
 * This class helps us to deal with logging.
 */
public class LoggingTool extends Formatter {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    private final Formatter formatter = new SimpleFormatter();

    private LoggingTool() {
        // prevent instantiation
    }

    /**
     * Log report in colors.
     *
     * @param logger instance of {@link Logger}
     * @param report report to log
     */
    public static void logResults(final Logger logger, final Report report) {
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
    public static void logRedMessage(final Logger logger, final Level level, final String message) {
        logger.log(level, () -> ANSI_RED + message + ANSI_RESET);
    }

    private static void logGreenMessage(final Logger logger, final Level level, final String message) {
        logger.log(level, () -> ANSI_GREEN + message + ANSI_RESET);
    }

    @Override
    public String format(final LogRecord record) {
        if (Level.WARNING.equals(record.getLevel())) {
            record.setMessage(ANSI_RED + record.getMessage() + ANSI_RESET);
        }
        return this.formatter.format(record);
    }
}