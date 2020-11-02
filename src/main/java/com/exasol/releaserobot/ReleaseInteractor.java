package com.exasol.releaserobot;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releaserobot.report.*;

/**
 * Implements release-related logic.
 */
public class ReleaseInteractor implements ReleaseUseCase {
    private static final Logger LOGGER = Logger.getLogger(ReleaseInteractor.class.getName());
    private final ValidateUseCase validateUseCase;
    private final Set<Platform> platforms;

    /**
     * Create a new instance of {@link ReleaseInteractor}.
     * 
     * @param validateUseCase validate use case for validating the platforms
     * @param platforms       set of platforms to perform release on
     */
    public ReleaseInteractor(final ValidateUseCase validateUseCase, final Set<Platform> platforms) {
        this.validateUseCase = validateUseCase;
        this.platforms = platforms;
    }

    @Override
    public List<Report> release(final UserInput userInput) {
        final List<Report> reports = new ArrayList<>();
        final ValidationReport validationReport = this.validateUseCase.validate(userInput);
        reports.add(validationReport);
        if (!validationReport.hasFailures()) {
            LOGGER.info(() -> "Release started.");
            final ReleaseReport releaseReport = this.makeRelease(userInput);
            logResults(Goal.RELEASE, releaseReport);
            reports.add(releaseReport);
        }
        return reports;
    }

    private ReleaseReport makeRelease(final UserInput userInput) {
        final ReleaseReport releaseReport = new ReleaseReport();
        for (final Platform platform : this.platforms) {
            try {
                platform.release(userInput);
                releaseReport.addSuccessfulRelease(platform.getPlatformName());
            } catch (final Exception exception) {
                releaseReport.addFailedRelease(platform.getPlatformName(), ExceptionUtils.getStackTrace(exception));
                break;
            }
        }
        return releaseReport;
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