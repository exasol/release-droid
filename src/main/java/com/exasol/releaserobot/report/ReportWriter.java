package com.exasol.releaserobot.report;

import static com.exasol.releaserobot.ReleaseRobotConstants.LINE_SEPARATOR;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.releaserobot.UserInput;

/**
 * This class writes reports from {@link ValidationReport} to a file.
 */
public class ReportWriter {
    private static final Logger LOGGER = Logger.getLogger(ReportWriter.class.getName());
    private final UserInput userInput;
    private final Path reportPath;

    /**
     * Create a new instance of {@link ReportWriter}.
     *
     * @param userInput instance of {@link UserInput}
     */
    public ReportWriter(final UserInput userInput, final Path reportPath) {
        this.userInput = userInput;
        this.reportPath = reportPath;
    }

    /**
     * Write a validation report to the file.
     *
     * @param reports one or more reports to write
     */
    // [impl->dsn~rr-writes-report-to-file~1]
    public void writeValidationReportToFile(final List<Report> reports) {
        final File reportFile = prepareFile();
        try (final FileWriter writer = new FileWriter(reportFile.getAbsoluteFile())) {
            writeExecutionInformation(writer);
            writeReports(writer, reports);
        } catch (final IOException exception) {
            throw new IllegalStateException("E-RR-RW-2: Unable to write a report.", exception);
        }
        LOGGER.info(() -> "A full report is available: " + this.reportPath.toString());
    }

    private void writeReports(final FileWriter writer, final List<Report> reports) throws IOException {
        for (final Report report : reports) {
            println(writer, report.getShortDescription());
            println(writer, report.getFullReport());
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
        println(writer,
                "Repository: " + this.userInput.getRepositoryOwner() + "." + this.userInput.getRepositoryName());
        println(writer, "Platforms: "
                + this.userInput.getPlatformNames().stream().map(Enum::name).collect(Collectors.joining(", ")));
        if (this.userInput.hasGitBranch()) {
            println(writer, "Git branch: " + this.userInput.getGitBranch());
        }
        println(writer);
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

    private void logFilePreparation(final boolean createdNewFile) {
        if (createdNewFile) {
            LOGGER.fine("Creating a new file for a report.");
        } else {
            LOGGER.fine("Re-writing the previous report.");
        }
    }
}