package com.exasol.releaserobot;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releaserobot.Platform.PlatformName;
import com.exasol.releaserobot.report.*;

/**
 * Implements the Release use case.
 */
public class ReleaseInteractor implements ReleaseUseCase {
    private static final Logger LOGGER = Logger.getLogger(ReleaseInteractor.class.getName());
    private final ValidateUseCase validateUseCase;
    private final Map<PlatformName, ReleaseMaker> releaseMakers;

    /**
     * Create a new instance of {@link ReleaseInteractor}.
     *
     * @param validateUseCase validate use case for validating the platforms
     * @param platforms       set of platforms to perform release on
     */
    public ReleaseInteractor(final ValidateUseCase validateUseCase,
            final Map<PlatformName, ReleaseMaker> releaseMakers) {
        this.validateUseCase = validateUseCase;
        this.releaseMakers = releaseMakers;
    }

    @Override
    public List<Report> release(final UserInput userInput) {
        final List<Report> reports = new ArrayList<>();
        final ValidationReport validationReport = this.validateUseCase.validate(userInput);
        reports.add(validationReport);
        if (!validationReport.hasFailures()) {
            LOGGER.info(() -> "Release started.");
            final ReleaseReport releaseReport = this.makeRelease(userInput.getPlatformNames());
            logResults(Goal.RELEASE, releaseReport);
            reports.add(releaseReport);
        }
        return reports;
    }

    private ReleaseReport makeRelease(final Set<PlatformName> platformNames) {
        final ReleaseReport releaseReport = new ReleaseReport();
        for (final PlatformName platformName : platformNames) {
            try {
                this.getReleaseMaker(platformName).makeRelease();
                releaseReport.addSuccessfulRelease(platformName);
            } catch (final Exception exception) {
                releaseReport.addFailedRelease(platformName, ExceptionUtils.getStackTrace(exception));
                break;
            }
        }
        return releaseReport;
    }

    private ReleaseMaker getReleaseMaker(final PlatformName platformName) {
        return this.releaseMakers.get(platformName);
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
