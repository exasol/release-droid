package com.exasol.releasedroid.main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.exasol.releasedroid.formatting.SummaryFormatter;
import com.exasol.releasedroid.usecases.logging.ReportFormatter;
import com.exasol.releasedroid.usecases.release.ReleaseUseCase;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.request.Goal;
import com.exasol.releasedroid.usecases.request.UserInput;
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
    private final SummaryWriter summaryWriter;

    private ReleaseDroid(final ValidateUseCase validateUseCase, final ReleaseUseCase releaseUseCase) {
        this.releaseUseCase = releaseUseCase;
        this.validateUseCase = validateUseCase;
        this.summaryWriter = new SummaryWriter(new SummaryFormatter(new ReportFormatter()));
    }

    public static ReleaseDroid of(final ValidateUseCase validateUseCase) {
        return new ReleaseDroid(validateUseCase, null);
    }

    public static ReleaseDroid of(final ValidateUseCase validateUseCase, final ReleaseUseCase releaseUseCase) {
        return new ReleaseDroid(validateUseCase, releaseUseCase);
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
        } else if (userInput.getGoal() == Goal.RELEASE && this.releaseUseCase != null) {
            reports.addAll(this.releaseUseCase.release(userInput));
        }
        writeReportToDisk(userInput, reports);
    }

    private void writeReportToDisk(final UserInput userInput, final List<Report> reports) {
        this.summaryWriter.writeResponseToDisk(REPORT_PATH, userInput, reports);
    }
}