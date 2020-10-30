package com.exasol.releaserobot;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.exasol.releaserobot.report.*;

/**
 * This class is the main entry point for calls to a Release Robot.
 */
public class ReleaseRobot {
    private static final Logger LOGGER = Logger.getLogger(ReleaseRobot.class.getName());
    private static final String HOME_DIRECTORY = System.getProperty("user.home");
    private static final Path REPORT_PATH = Paths.get(HOME_DIRECTORY, ".release-robot", "last_report.txt");
    private final RepositoryHandler repositoryHandler;

    public ReleaseRobot(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }

    /**
     * Main entry point for all Release Robot's calls.
     */
    // [impl->dsn~rr-starts-release-only-if-all-validation-succeed~1]
    // [impl->dsn~rr-runs-release-goal~1]
    public void run(final UserInput userInput) {
        LOGGER.fine(() -> "Release Robot has received '" + userInput.getGoal() + "' request for the project "
                + userInput.getRepositoryName() + ".");
        final List<Report> reports = new ArrayList<>();
        final ValidationReport validationReport = this.validate(userInput);
        reports.add(validationReport);
        if (userInput.getGoal() == Goal.RELEASE && !validationReport.hasFailures()) {
            reports.add(this.release(this.repositoryHandler));
        }
        new ReportWriter(userInput, REPORT_PATH).writeValidationReportToFile(reports);
    }

    private ValidationReport validate(final UserInput userInput) {
        final ValidationReport validationReport = runValidation(userInput, this.repositoryHandler);
        logResults(Goal.VALIDATE, validationReport);
        return validationReport;
    }

    private ReleaseReport release(final RepositoryHandler repositoryHandler) {
        final ReleaseReport releaseReport = repositoryHandler.release();
        logResults(Goal.RELEASE, releaseReport);
        return releaseReport;
    }

    // [impl->dsn~rr-runs-validate-goal~1]
    private ValidationReport runValidation(final UserInput userInput, final RepositoryHandler repositoryHandler) {
        if (validateUserSpecifiedBranch(userInput)) {
            return repositoryHandler.validate(userInput.getGitBranch());
        } else {
            return repositoryHandler.validate();
        }
    }

    private boolean validateUserSpecifiedBranch(final UserInput userInput) {
        return userInput.getGoal() != Goal.RELEASE && userInput.hasGitBranch();
    }

    // [impl->dsn~rr-creates-validation-report~1]
    // [impl->dsn~rr-creates-release-report~1]
    private void logResults(final Goal goal, final Report report) {
        if (report.hasFailures()) {
            LOGGER.severe(() -> "'" + goal + "' request failed: " + report.getFailuresReport());
        } else {
            LOGGER.info(report.getShortDescription());
        }
    }
}