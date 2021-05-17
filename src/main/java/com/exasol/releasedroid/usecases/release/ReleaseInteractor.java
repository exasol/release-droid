package com.exasol.releasedroid.usecases.release;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_STATE_DIRECTORY;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.logging.ReportLogger;
import com.exasol.releasedroid.usecases.report.ReleaseResult;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ValidateUseCase;

/**
 * Implements the Release use case.
 */
public class ReleaseInteractor implements ReleaseUseCase {
    private static final Logger LOGGER = Logger.getLogger(ReleaseInteractor.class.getName());
    private final ValidateUseCase validateUseCase;
    private final Map<PlatformName, ReleaseMaker> releaseMakers;
    private final ReportLogger reportLogger = new ReportLogger();
    private final ReleaseState releaseState = new ReleaseState(RELEASE_DROID_STATE_DIRECTORY);
    private final ReleaseManager releaseManager;

    /**
     * Create a new instance of {@link ReleaseInteractor}.
     *
     * @param validateUseCase validate use case for validating the platforms
     * @param releaseMakers   map with platform names and release makers
     * @param releaseManager  instance of {@link ReleaseManager}
     */
    public ReleaseInteractor(final ValidateUseCase validateUseCase, final Map<PlatformName, ReleaseMaker> releaseMakers,
            final ReleaseManager releaseManager) {
        this.validateUseCase = validateUseCase;
        this.releaseMakers = releaseMakers;
        this.releaseManager = releaseManager;
    }

    @Override
    // [impl->dsn~rd-starts-release-only-if-all-validation-succeed~1]
    // [impl->dsn~rd-runs-release-goal~1]
    public List<Report> release(final Repository repository, final List<PlatformName> platforms) {
        try {
            return this.attemptToRelease(repository, platforms);
        } catch (final Exception exception) {
            throw new ReleaseException(exception);
        }
    }

    private List<Report> attemptToRelease(final Repository repository, final List<PlatformName> platforms) {
        final List<Report> reports = new ArrayList<>();
        final Report validationReport = this.validateUseCase.validate(repository, platforms);
        reports.add(validationReport);
        if (!validationReport.hasFailures()) {
            LOGGER.info(() -> "Release started.");
            final Report releaseReport = this.makeRelease(repository, platforms);
            logResults(releaseReport);
            reports.add(releaseReport);
        }
        return reports;
    }

    private void logResults(final Report releaseReport) {
        this.reportLogger.logResults(releaseReport);
    }

    private Report makeRelease(final Repository repository, final List<PlatformName> platforms) {
        final var report = Report.releaseReport();
        final Set<PlatformName> released = getAlreadyReleasedPlatforms(repository.getName(), repository.getVersion());
        if (unreleasedPlatformsPresent(platforms, released)) {
            report.merge(release(repository, platforms, released));
        } else {
            LOGGER.info(() -> "Nothing to release. The release has been already performed on all mentioned platforms.");
        }
        return report;
    }

    private Set<PlatformName> getAlreadyReleasedPlatforms(final String repositoryName, final String releaseVersion) {
        return this.releaseState.getProgress(repositoryName, releaseVersion);
    }

    private boolean unreleasedPlatformsPresent(final List<PlatformName> platforms, final Set<PlatformName> released) {
        return !released.containsAll(platforms);
    }

    private Report release(final Repository repository, final List<PlatformName> platforms,
            final Set<PlatformName> released) {
        prepareRepositoryForRelease(repository);
        final Report report = releaseOnPlatforms(repository, platforms, released);
        cleanRepositoryAfterRelease(repository, report.hasFailures());
        return report;
    }

    private Report releaseOnPlatforms(final Repository repository, final List<PlatformName> platforms,
            final Set<PlatformName> released) {
        final Report report = Report.releaseReport();
        for (final PlatformName platformName : platforms) {
            if (released.contains(platformName)) {
                LOGGER.info(
                        () -> "Skipping " + platformName + " platform, the release has been already performed there.");
            } else {
                final Report platformReport = releaseOnPlatform(repository, platformName);
                report.merge(platformReport);
                if (platformReport.hasFailures()) {
                    break;
                }
            }
        }
        return report;
    }

    private Report releaseOnPlatform(final Repository repository, final PlatformName platformName) {
        final var report = Report.releaseReport();
        try {
            LOGGER.info(() -> "Releasing on " + platformName + " platform.");
            getReleaseMaker(platformName).makeRelease(repository);
            saveProgress(platformName, repository);
            report.addResult(ReleaseResult.successfulRelease(platformName));
        } catch (final Exception exception) {
            report.addResult(ReleaseResult.failedRelease(platformName, ExceptionUtils.getStackTrace(exception)));
        }
        return report;
    }

    private void saveProgress(final PlatformName platformName, final Repository repository) {
        this.releaseState.saveProgress(repository.getName(), repository.getVersion(), platformName);
    }

    private void prepareRepositoryForRelease(final Repository repository) {
        this.releaseManager.prepareForRelease(repository);
    }

    private void cleanRepositoryAfterRelease(final Repository repository, final boolean hasFailures) {
        if (!hasFailures) {
            this.releaseManager.cleanUpAfterRelease(repository);
        }
    }

    private ReleaseMaker getReleaseMaker(final PlatformName platformName) {
        return this.releaseMakers.get(platformName);
    }
}