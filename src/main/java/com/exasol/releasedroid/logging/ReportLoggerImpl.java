package com.exasol.releasedroid.logging;

import java.util.logging.Logger;

import com.exasol.releasedroid.formatting.ReportFormatter;
import com.exasol.releasedroid.usecases.logging.ReportLogger;
import com.exasol.releasedroid.usecases.report.Report;

/**
 * Implementation of {@link ReportLogger}.
 */
public class ReportLoggerImpl implements ReportLogger {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final Logger LOGGER = Logger.getLogger(ReportLoggerImpl.class.getName());
    private final ReportFormatter reportFormatter;

    public ReportLoggerImpl(final ReportFormatter reportFormatter) {
        this.reportFormatter = reportFormatter;
    }

    /**
     * Log report results.
     *
     * @param report report to log
     */
    @Override
    public void logResults(final Report report) {
        if (report.hasFailures()) {
            LOGGER.severe(() -> this.reportFormatter.formatReportWithFailuresOnly(report));
        } else {
            LOGGER.info(() -> ANSI_GREEN + this.reportFormatter.formatHeader(report) + ANSI_RESET);
        }
    }
}
