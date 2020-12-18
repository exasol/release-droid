package com.exasol.releasedroid.usecases.release;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releasedroid.usecases.*;
import com.exasol.releasedroid.usecases.logging.ReportLogger;
import com.exasol.releasedroid.usecases.report.ReleaseResult;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.validate.ValidateUseCase;

/**
 * Implements the Release use case.
 */
public class ReleaseInteractor implements ReleaseUseCase {
    private static final Logger LOGGER = Logger.getLogger(ReleaseInteractor.class.getName());
    private final ValidateUseCase validateUseCase;
    private final Map<PlatformName, ? extends ReleaseMaker> releaseMakers;
    private final RepositoryGateway repositoryGateway;
    private final RepositoryModifier repositoryModifier;
    private final ReportLogger reportLogger = new ReportLogger();

    /**
     * Create a new instance of {@link ReleaseInteractor}.
     * 
     * @param validateUseCase    validate use case for validating the platforms
     * @param releaseMakers      map with platform names and release makers
     * @param repositoryGateway  instance of {@link RepositoryGateway}
     * @param repositoryModifier instance of {@link RepositoryModifier}
     */
    public ReleaseInteractor(final ValidateUseCase validateUseCase,
            final Map<PlatformName, ? extends ReleaseMaker> releaseMakers, final RepositoryGateway repositoryGateway,
            final RepositoryModifier repositoryModifier) {
        this.validateUseCase = validateUseCase;
        this.releaseMakers = releaseMakers;
        this.repositoryGateway = repositoryGateway;
        this.repositoryModifier = repositoryModifier;
    }

    @Override
    // [impl->dsn~rr-starts-release-only-if-all-validation-succeed~1]
    // [impl->dsn~rr-runs-release-goal~1]
    public List<Report> release(final UserInput userInput) {
        final List<Report> reports = new ArrayList<>();
        final Report validationReport = this.validateUseCase.validate(userInput);
        reports.add(validationReport);
        if (!validationReport.hasFailures()) {
            LOGGER.info(() -> "Release started.");
            final Report releaseReport = this.makeRelease(userInput);
            logResults(releaseReport);
            reports.add(releaseReport);
        }
        return reports;
    }

    private void logResults(final Report releaseReport) {
        this.reportLogger.logResults(releaseReport);
    }

    private Report makeRelease(final UserInput userInput) {
        final Report report = Report.releaseReport();
        final Repository repository = this.repositoryGateway.getRepository(userInput);
        prepareRepositoryForRelease(repository);
        for (final PlatformName platformName : userInput.getPlatformNames()) {
            LOGGER.info(() -> "Releasing on " + platformName + " platform.");
            try {
                this.getReleaseMaker(platformName).makeRelease(repository);
                report.addResult(ReleaseResult.successfulRelease(platformName));
            } catch (final ReleaseException exception) {
                report.addResult(ReleaseResult.failedRelease(platformName, ExceptionUtils.getStackTrace(exception)));
                break;
            }
        }
        return report;
    }

    private void prepareRepositoryForRelease(final Repository repository) {
        this.repositoryModifier.writeReleaseDate(repository);
    }

    private ReleaseMaker getReleaseMaker(final PlatformName platformName) {
        return this.releaseMakers.get(platformName);
    }
}