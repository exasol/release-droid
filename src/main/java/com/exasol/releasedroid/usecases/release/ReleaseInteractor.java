package com.exasol.releasedroid.usecases.release;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.logging.ReportLogger;
import com.exasol.releasedroid.usecases.report.ReleaseResult;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.request.UserInput;
import com.exasol.releasedroid.usecases.validate.ValidateUseCase;

/**
 * Implements the Release use case.
 */
public class ReleaseInteractor implements ReleaseUseCase {
    private static final Logger LOGGER = Logger.getLogger(ReleaseInteractor.class.getName());
    private final ValidateUseCase validateUseCase;
    private final Map<PlatformName, ReleaseMaker> releaseMakers;
    private final RepositoryGateway repositoryGateway;
    private final ReportLogger reportLogger = new ReportLogger();
    private final ReleaseManager releaseManager;

    /**
     * Create a new instance of {@link ReleaseInteractor}.
     *
     * @param validateUseCase   validate use case for validating the platforms
     * @param releaseMakers     map with platform names and release makers
     * @param repositoryGateway instance of {@link RepositoryGateway}
     * @param releaseManager    instance of {@link ReleaseManager}
     */
    public ReleaseInteractor(final ValidateUseCase validateUseCase, final Map<PlatformName, ReleaseMaker> releaseMakers,
            final RepositoryGateway repositoryGateway, final ReleaseManager releaseManager) {
        this.validateUseCase = validateUseCase;
        this.releaseMakers = releaseMakers;
        this.repositoryGateway = repositoryGateway;
        this.releaseManager = releaseManager;
    }

    @Override
    // [impl->dsn~rd-starts-release-only-if-all-validation-succeed~1]
    // [impl->dsn~rd-runs-release-goal~1]
    public List<Report> release(final UserInput userInput) {
        try {
            return this.attemptToRelease(userInput);
        } catch (final Exception exception) {
            throw new ReleaseException(exception);
        }
    }

    private List<Report> attemptToRelease(final UserInput userInput) {
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
        report.merge(releaseOnPlatforms(userInput, repository));
        cleanRepositoryAfterRelease(repository, report);
        return report;
    }

    private Report releaseOnPlatforms(final UserInput userInput, final Repository repository) {
        final Report report = Report.releaseReport();
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
        this.releaseManager.prepareForRelease(repository);
    }

    private void cleanRepositoryAfterRelease(final Repository repository, final Report report) {
        if (!report.hasFailures()) {
            this.releaseManager.cleanUpAfterRelease(repository);
        }
    }

    private ReleaseMaker getReleaseMaker(final PlatformName platformName) {
        return this.releaseMakers.get(platformName);
    }
}