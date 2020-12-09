package com.exasol.releasedroid.report;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.releasedroid.usecases.UserInput;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ReportFormatter;

/**
 * This class writes reports from {@link Report} to a file.
 */
public class ReportWriter {
    private static final Logger LOGGER = Logger.getLogger(ReportWriter.class.getName());
    private final UserInput userInput;
    private final Path reportPath;
    private final ReportFormatter reportFormatter;

    /**
     * Create a new instance of {@link ReportWriter}.
     *
     * @param userInput       instance of {@link UserInput}
     * @param reportPath      path to the file on disk
     * @param reportFormatter formatter for reports
     */
    public ReportWriter(final UserInput userInput, final Path reportPath, final ReportFormatter reportFormatter) {
        this.userInput = userInput;
        this.reportPath = reportPath;
        this.reportFormatter = reportFormatter;
    }

    /**
     * Write a list of reports to a single file on disk.
     *
     * @param reports one or more reports to write
     */
    // [impl->dsn~rr-writes-report-to-file~1]
    public void writeReportsToFile(final List<Report> reports) {
        final File reportFile = prepareFile();
        try (final FileWriter writer = new FileWriter(reportFile.getAbsoluteFile())) {
            writeExecutionInformation(writer);
            writeReports(writer, reports);
        } catch (final IOException exception) {
            throw new IllegalStateException("E-RR-RW-2: Unable to write a report.", exception);
        }
        LOGGER.info(() -> "A full report is available: " + this.reportPath.toString());
    }

    private File prepareFile() {
        final File reportFile = this.reportPath.toFile();
        try {
            final boolean createdNewFile = reportFile.createNewFile();
            logFilePreparation(createdNewFile);
        } catch (final IOException exception) {
            throw new IllegalStateException("E-RR-RW-1: Unable to prepare a file for a report.", exception);
        }
        return reportFile;
    }

    private void writeReports(final FileWriter writer, final List<Report> reports) throws IOException {
        for (final Report report : reports) {
            println(writer, this.reportFormatter.formatReport(report));
            println(writer);
        }
    }

    private void println(final FileWriter writer, final String string) throws IOException {
        writer.write(string + LINE_SEPARATOR);
    }

    private void println(final FileWriter writer) throws IOException {
        println(writer, "");
    }

    private void writeExecutionInformation(final FileWriter writer) throws IOException {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        println(writer, now);
        println(writer);
        println(writer, "Goal: " + this.userInput.getGoal());
        println(writer, "Repository: " + this.userInput.getRepositoryName());
        println(writer, "Platforms: "
                + this.userInput.getPlatformNames().stream().map(Enum::name).collect(Collectors.joining(", ")));
        if (this.userInput.hasBranch()) {
            println(writer, "Git branch: " + this.userInput.getBranch());
        }
        println(writer);
    }

    private void logFilePreparation(final boolean createdNewFile) {
        if (createdNewFile) {
            LOGGER.fine("Creating a new file for a report.");
        } else {
            LOGGER.fine("Re-writing the previous report.");
        }
    }
}