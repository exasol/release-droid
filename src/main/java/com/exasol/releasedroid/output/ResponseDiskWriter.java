package com.exasol.releasedroid.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.formatting.HeaderFormatter;
import com.exasol.releasedroid.main.ReleaseDroidResponseConsumer;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.response.ReleaseDroidResponse;

/**
 * This class writes response summary to a file.
 */
public class ResponseDiskWriter implements ReleaseDroidResponseConsumer {
    private static final Logger LOGGER = Logger.getLogger(ResponseDiskWriter.class.getName());
    private final HeaderFormatter headerFormatter = new HeaderFormatter();
    private final ReportFormatter reportFormatter;
    private final Path pathToWrite;

    /**
     * Create a new instance of {@link ResponseDiskWriter}.
     *
     * @param reportFormatter formatter
     * @param pathToWrite     path to the file to write to
     */
    public ResponseDiskWriter(final ReportFormatter reportFormatter, final Path pathToWrite) {
        this.reportFormatter = reportFormatter;
        this.pathToWrite = pathToWrite;
    }

    @Override
    // [impl->dsn~rd-writes-report-to-file~1]
    public void consumeResponse(final ReleaseDroidResponse response) {
        final File fileForSummary = prepareFile(this.pathToWrite);
        final String summary = prepareSummary(response);
        try (final var writer = new FileWriter(fileForSummary.getAbsoluteFile())) {
            writer.write(summary);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-RD-10").message("Unable to write a report.").toString(), exception);
        }
        LOGGER.info(() -> "A full report is available: " + this.pathToWrite);
    }

    private String prepareSummary(final ReleaseDroidResponse response) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(this.headerFormatter.formatHeader(response));
        for (final Report report : response.getReports()) {
            stringBuilder.append(this.reportFormatter.formatReport(report));
        }
        return stringBuilder.toString();
    }

    private File prepareFile(final Path reportPath) {
        final File reportFile = reportPath.toFile();
        try {
            final boolean createdNewFile = reportFile.createNewFile();
            logFilePreparation(createdNewFile);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-RD-5").message("Unable to prepare a file for a report.").toString(),
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