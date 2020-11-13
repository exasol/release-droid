package com.exasol.releaserobot.usecases.release;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releaserobot.usecases.*;
import com.exasol.releaserobot.usecases.validate.RepositoryGateway;
import com.exasol.releaserobot.usecases.validate.ValidateUseCase;

/**
 * Implements the Release use case.
 */
public class ReleaseInteractor implements ReleaseUseCase {
    private static final Logger LOGGER = Logger.getLogger(ReleaseInteractor.class.getName());
    private final ValidateUseCase validateUseCase;
    private final Map<PlatformName, ? extends ReleaseMaker> releaseMakers;
    private final RepositoryGateway repositoryGateway;

    /**
     * Create a new instance of {@link ReleaseInteractor}.
     *
     * @param validateUseCase   validate use case for validating the platforms
     * @param releaseMakers     map with platform names and release makers
     * @param repositoryGateway instance of {@link RepositoryGateway]}
     */
    public ReleaseInteractor(final ValidateUseCase validateUseCase,
            final Map<PlatformName, ? extends ReleaseMaker> releaseMakers, final RepositoryGateway repositoryGateway) {
        this.validateUseCase = validateUseCase;
        this.releaseMakers = releaseMakers;
        this.repositoryGateway = repositoryGateway;
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
            final Report releaseReport = this.makeRelease(userInput.getRepositoryName(), userInput.getPlatformNames());
            logResults(Goal.RELEASE, releaseReport);
            reports.add(releaseReport);
        }
        return reports;
    }

    private Report makeRelease(final String repositoryFullName, final Set<PlatformName> platformNames) {
        final Report report = ReportImpl.releaseReport();
        final Repository repository = this.repositoryGateway.getRepositoryWithDefaultBranch(repositoryFullName);
        for (final PlatformName platformName : platformNames) {
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

    private ReleaseMaker getReleaseMaker(final PlatformName platformName) {
        return this.releaseMakers.get(platformName);
    }

    private void logResults(final Goal goal, final Report report) {
        if (report.hasFailures()) {
            LOGGER.severe(() -> "'" + goal + "' request failed: " + report.getFailuresReport());
        } else {
            LOGGER.info(report.getShortDescription());
        }
    }
}
