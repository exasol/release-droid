package com.exasol;

import java.io.*;
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
    private static final String RELEASE_ROBOT_REPORT = "/.release-robot/last_report.txt";
    private static final String HOME_DIRECTORY = System.getProperty("user.home");
    private static final String REPORT_PATH = HOME_DIRECTORY + RELEASE_ROBOT_REPORT;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
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
    public void writeValidationReportToFile(final ValidationReport validationReport) {
        final File reportFile = prepareFile();
        try (final FileWriter writer = new FileWriter(reportFile, false)) {
            writeReport(validationReport, writer);
        } catch (final IOException exception) {
            throw new IllegalStateException("E-RR-RW-2: Unable to write a report.");
        }
        LOGGER.info("A full report is available: " + REPORT_PATH);
    }

    private void writeReport(final ValidationReport validationReport, final FileWriter writer) throws IOException {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        writer.write(now + LINE_SEPARATOR);
        writer.write(LINE_SEPARATOR);
        writer.write("Goal: " + this.userInput.getGoal() + LINE_SEPARATOR);
        writer.write("Repository: " + this.userInput.getRepositoryOwner() + "." + this.userInput.getRepositoryName()
                + LINE_SEPARATOR);
        writer.write("Platforms: "
                + this.userInput.getPlatformNames().stream().map(Enum::name).collect(Collectors.joining(", "))
                + LINE_SEPARATOR);
        if (this.userInput.hasGitBranch()) {
            writer.write("Git branch: " + this.userInput.getGitBranch() + LINE_SEPARATOR);
        }
        if (validationReport.hasFailedValidations()) {
            writer.write("VALIDATION FAILED!" + LINE_SEPARATOR);
        } else {
            writer.write("Validation is successful!" + LINE_SEPARATOR);
        }
        writer.write(LINE_SEPARATOR);
        writer.write(validationReport.getFullReport());
    }

    private File prepareFile() {
        final File reportFile = new File(REPORT_PATH);
        try {
            final boolean createdNewFile = reportFile.createNewFile();
            logFilePreparation(createdNewFile);
        } catch (final IOException exception) {
            throw new IllegalStateException("E-RR-RW-1: Unable to prepare a file for a report.");
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