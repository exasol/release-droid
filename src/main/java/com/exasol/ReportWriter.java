package com.exasol;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.validation.ValidationReport;

/**
 * This class writes reports from {@link ValidationReport} to a file.
 */
public class ReportWriter {
    private static final Logger LOGGER = Logger.getLogger(ReportWriter.class.getName());
    private static final String HOME_DIRECTORY = System.getProperty("user.home");
    private static final Path REPORT_PATH = Paths.get(HOME_DIRECTORY, ".release-robot", "last_report.txt");
    private final UserInput userInput;

    /**
     * Create a new instance of {@link ReportWriter}.
     * 
     * @param userInput instance of {@link UserInput}
     */
    public ReportWriter(final UserInput userInput) {
        this.userInput = userInput;
    }

    /**
     * Write a validation report to the file.
     * 
     * @param validationReport report to write
     */
    // [impl->dsn~rr-writes-validation-report-to-file~1]
    public void writeValidationReportToFile(final ValidationReport validationReport) {
        final File reportFile = prepareFile();
        try (final PrintWriter writer = new PrintWriter(reportFile.getAbsoluteFile())) {
            writeReport(validationReport, writer);
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException("E-RR-RW-2: Unable to write a report.", exception);
        }
        LOGGER.info("A full report is available: " + REPORT_PATH.toString());
    }

    private void writeReport(final ValidationReport validationReport, final PrintWriter writer) {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        writer.println(now);
        writer.println();
        writer.println("Goal: " + this.userInput.getGoal());
        writer.println("Repository: " + this.userInput.getRepositoryOwner() + "." + this.userInput.getRepositoryName());
        writer.println("Platforms: "
                + this.userInput.getPlatformNames().stream().map(Enum::name).collect(Collectors.joining(", ")));
        if (this.userInput.hasGitBranch()) {
            writer.println("Git branch: " + this.userInput.getGitBranch());
        }
        if (validationReport.hasFailedValidations()) {
            writer.println("VALIDATION FAILED!");
        } else {
            writer.println("Validation is successful!");
        }
        writer.println();
        writer.println(validationReport.getFullReport());
    }

    private File prepareFile() {
        final File reportFile = REPORT_PATH.toFile();
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