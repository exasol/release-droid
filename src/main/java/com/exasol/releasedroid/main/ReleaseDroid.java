package com.exasol.releasedroid.main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.exasol.releasedroid.formatting.ReportFormatter;
import com.exasol.releasedroid.formatting.SummaryFormatter;
import com.exasol.releasedroid.logging.ReportLogger;
import com.exasol.releasedroid.usecases.Goal;
import com.exasol.releasedroid.usecases.UserInput;
import com.exasol.releasedroid.usecases.release.ReleaseUseCase;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.validate.ValidateUseCase;

/**
 * This class is the main entry point for calls to a Release Droid.
 */
public class ReleaseDroid {
    private static final Logger LOGGER = Logger.getLogger(ReleaseDroid.class.getName());
    private static final String HOME_DIRECTORY = System.getProperty("user.home");
    private static final Path REPORT_PATH = Paths.get(HOME_DIRECTORY, ".release-droid", "last_report.txt");
    private final ReleaseUseCase releaseUseCase;
    private final ValidateUseCase validateUseCase;
    private final ReportLogger reportLogger;
    private final ResponseWriter responseWriter;

    public ReleaseDroid(final ReleaseUseCase releaseUseCase, final ValidateUseCase validateUseCase) {
        this.releaseUseCase = releaseUseCase;
        this.validateUseCase = validateUseCase;
        this.responseWriter = new ResponseWriter(new SummaryFormatter(new ReportFormatter()));
        this.reportLogger = new ReportLogger(new ReportFormatter());
    }

    /**
     * Main entry point for all Release Droid's calls.
     */
    // [impl->dsn~rr-creates-validation-report~1]
    // [impl->dsn~rr-creates-release-report~1]
    public void run(final UserInput userInput) {
        LOGGER.fine(() -> "Release Droid has received '" + userInput.getGoal() + "' request for the project '"
                + userInput.getRepositoryName() + "'.");
        final List<Report> reports = new ArrayList<>();
        if (userInput.getGoal() == Goal.VALIDATE) {
            reports.add(this.validateUseCase.validate(userInput));
        } else if (userInput.getGoal() == Goal.RELEASE) {
            reports.addAll(this.releaseUseCase.release(userInput));
        }
        this.logResults(reports);
        writeResponseToDisk(userInput, reports);
    }

    private void logResults(final List<Report> reports) {
        for (final Report report : reports) {
            this.reportLogger.logResults(report);
        }
    }

    private void writeResponseToDisk(final UserInput userInput, final List<Report> reports) {
        this.responseWriter.writeResponseToDisk(REPORT_PATH, userInput, reports);
    }
}