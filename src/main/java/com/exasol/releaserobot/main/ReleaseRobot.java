package com.exasol.releaserobot.main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.report.ReportWriter;
import com.exasol.releaserobot.usecases.*;
import com.exasol.releaserobot.usecases.release.ReleaseUseCase;
import com.exasol.releaserobot.usecases.validate.ValidateUseCase;

/**
 * This class is the main entry point for calls to a Release Robot.
 */
public class ReleaseRobot {
    private static final Logger LOGGER = Logger.getLogger(ReleaseRobot.class.getName());
    private static final String HOME_DIRECTORY = System.getProperty("user.home");
    private static final Path REPORT_PATH = Paths.get(HOME_DIRECTORY, ".release-robot", "last_report.txt");
    private final ReleaseUseCase releaseUseCase;
    private final ValidateUseCase validateUseCase;

    public ReleaseRobot(final ReleaseUseCase releaseUseCase, final ValidateUseCase validateUseCase) {
        this.releaseUseCase = releaseUseCase;
        this.validateUseCase = validateUseCase;
    }

    /**
     * Main entry point for all Release Robot's calls.
     *
     * @throws GitHubException if some problem occurs
     */
    // [impl->dsn~rr-creates-validation-report~1]
    // [impl->dsn~rr-creates-release-report~1]
    public void run(final UserInput userInput) throws GitHubException {
        LOGGER.fine(() -> "Release Robot has received '" + userInput.getGoal() + "' request for the project '"
                + userInput.getRepositoryName() + "'.");
        final List<Report> reports = new ArrayList<>();
        if (userInput.getGoal() == Goal.VALIDATE) {
            reports.add(this.validateUseCase.validate(userInput));
        } else if (userInput.getGoal() == Goal.RELEASE) {
            reports.addAll(this.releaseUseCase.release(userInput));
        }
        // TODO: this should part of the usecases
        new ReportWriter(userInput, REPORT_PATH).writeValidationReportToFile(reports);
    }
}