package com.exasol.releasedroid.output;

import java.util.logging.Logger;

import com.exasol.releasedroid.main.ReleaseDroidResponseConsumer;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.response.ReleaseDroidResponse;

/**
 * This class logs the release droid response.
 */
public class ResponseLogger implements ReleaseDroidResponseConsumer {
    private static final Logger LOGGER = Logger.getLogger(ResponseLogger.class.getName());
    private final ReportFormatter reportFormatter;

    /**
     * Create a new instance of {@link ResponseLogger}.
     *
     * @param reportFormatter report formatter
     */
    public ResponseLogger(final ReportFormatter reportFormatter) {
        this.reportFormatter = reportFormatter;
    }

    @Override
    public void consumeResponse(final ReleaseDroidResponse response) {
        for (final Report report : response.getReports()) {
            logResults(report);
        }
    }

    private void logResults(final Report report) {
        final String formattedReport = this.reportFormatter.formatReport(report);
        if (report.hasFailures()) {
            LOGGER.severe(() -> formattedReport);
        } else {
            LOGGER.info(() -> formattedReport);
        }
    }
}
