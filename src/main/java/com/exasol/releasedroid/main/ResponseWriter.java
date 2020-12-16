package com.exasol.releasedroid.main;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.formatting.SummaryFormatter;
import com.exasol.releasedroid.usecases.UserInput;
import com.exasol.releasedroid.usecases.report.Report;

/**
 * This class writes reports from {@link Report} to a file.
 */
public class ResponseWriter {
    private static final Logger LOGGER = Logger.getLogger(ResponseWriter.class.getName());
    private final SummaryFormatter responseFormatter;

    /**
     * Create a new instance of {@link ResponseWriter}.
     *
     * @param responseFormatter formatter for the user response
     */
    public ResponseWriter(final SummaryFormatter responseFormatter) {
        this.responseFormatter = responseFormatter;
    }

    /**
     * Write a list of reports to a single file on disk.
     *
     * @param reportPath path to the file on disk
     * @param userInput  instance of {@link UserInput}
     * @param reports    list of reports to write
     */
    // [impl->dsn~rr-writes-report-to-file~1]
    public void writeResponseToDisk(final Path reportPath, final UserInput userInput, final List<Report> reports) {
        final File reportFile = prepareFile(reportPath);
        try (final FileWriter writer = new FileWriter(reportFile.getAbsoluteFile())) {
            writer.write(this.responseFormatter.formatResponse(userInput, reports));
        } catch (final IOException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-RR-RW-2").message("Unable to write a report.").toString(), exception);
        }
        LOGGER.info(() -> "A full report is available: " + reportPath.toString());
    }

    private File prepareFile(final Path reportPath) {
        final File reportFile = reportPath.toFile();
        try {
            final boolean createdNewFile = reportFile.createNewFile();
            logFilePreparation(createdNewFile);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-RR-RW-1").message("Unable to prepare a file for a report.").toString(),
                    exception);
        }
        return reportFile;
    }

    private void logFilePreparation(final boolean createdNewFile) {
        if (createdNewFile) {
            LOGGER.fine("Creating a new file for a report.");
        } else {
            LOGGER.fine("Re-writing the previous report.");
        }
    }
}
