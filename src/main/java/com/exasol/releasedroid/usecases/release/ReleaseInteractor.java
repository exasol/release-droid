package com.exasol.releasedroid.usecases.release;

import static com.exasol.errorreporting.ExaError.messageBuilder;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_STATE_DIRECTORY;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;
import com.exasol.releasedroid.usecases.UseCase;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.report.*;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.request.ReleasePlatforms;

/**
 * Implements the Release use case.
 */
public class ReleaseInteractor implements UseCase {
    private static final Logger LOGGER = Logger.getLogger(ReleaseInteractor.class.getName());
    private final UseCase validateUseCase;
    private final Map<PlatformName, ReleaseMaker> releaseMakers;
    private final ReleaseState releaseState;
    private final ReleaseManager releaseManager;

    /**
     * Create a new instance of {@link ReleaseInteractor}.
     *
     * @param validateUseCase validate use case for validating the platforms
     * @param releaseMakers   map with platform names and release makers
     * @param releaseManager  instance of {@link ReleaseManager}
     */
    public ReleaseInteractor(final UseCase validateUseCase, final Map<PlatformName, ReleaseMaker> releaseMakers,
            final ReleaseManager releaseManager) {
        this(validateUseCase, releaseMakers, releaseManager, new ReleaseState(RELEASE_DROID_STATE_DIRECTORY));
    }

    ReleaseInteractor(final UseCase validateUseCase, final Map<PlatformName, ReleaseMaker> releaseMakers,
            final ReleaseManager releaseManager, final ReleaseState releaseState) {
        this.validateUseCase = validateUseCase;
        this.releaseMakers = releaseMakers;
        this.releaseManager = releaseManager;
        this.releaseState = releaseState;
    }

    @Override
    // [impl->dsn~rd-starts-release-only-if-all-validation-succeed~1]
    // [impl->dsn~rd-runs-release-goal~1]
    public List<Report> apply(final Repository repository, final ReleasePlatforms platforms) {
        try {
            return makeRelease(repository, platforms);
        } catch (final Exception exception) {
            throw new ReleaseException(messageBuilder("E-RD-18").message("Error creating release").toString(),
                    exception);
        }
    }

    private List<Report> makeRelease(final Repository repository, final ReleasePlatforms platforms) {
        final Set<PlatformName> releasedPlatforms = getAlreadyReleasedPlatforms(repository.getName(),
                repository.getVersion());
        if (areUnreleasedPlatformsPresent(platforms.list(), releasedPlatforms)) {
            final ReleasePlatforms unreleasedPlatforms = platforms.withoutReleased(releasedPlatforms);
            return releaseOnPlatforms(repository, unreleasedPlatforms);
        } else {
            LOGGER.info(() -> "Nothing to release. The release has been already performed on all mentioned platforms.");
            return Collections.emptyList();
        }
    }

    @java.lang.SuppressWarnings("java:S135") // There is no a good workaround to avoid the second break here
    private List<Report> releaseOnPlatforms(final Repository repository, final ReleasePlatforms platforms) {
        final Progress progress = this.releaseManager.estimateDuration( //
                repository, estimateDuration(repository, platforms.list()));
        prepareRepositoryForRelease(repository);
        final var validationReport = ValidationReport.create();
        final var releaseReport = ReleaseReport.create();
        for (final PlatformName platform : platforms.list()) {
            final Report platformValidationReport = this.validateUseCase.apply(repository, platforms).get(0);
            validationReport.merge(platformValidationReport);
            if (!platformValidationReport.hasFailures()) {
                final var releaseReportForPlatform = releaseOnPlatform(repository, platform, progress);
                releaseReport.merge(releaseReportForPlatform);
                if (releaseReportForPlatform.hasFailures()) {
                    break;
                }
            } else {
                LOGGER.warning(() -> messageBuilder("W-RD-17")
                        .message("Validation for a platform {{platform name}} failed. Release is interrupted.",
                                platform.name())
                        .toString());
                break;
            }
        }
        if (!releaseReport.hasFailures()) {
            cleanRepositoryAfterRelease(repository);
        }
        return List.of(validationReport, releaseReport);
    }

    // [impl->dsn~estimate-duration~1]
    private Estimation estimateDuration(final Repository repository, final List<PlatformName> platforms) {
        Estimation estimation = Estimation.empty();
        for (final PlatformName platform : platforms) {
            estimation = estimation.plus(getReleaseMaker(platform).estimateDuration(repository));
        }
        return estimation;
    }

    private Set<PlatformName> getAlreadyReleasedPlatforms(final String repositoryName, final String releaseVersion) {
        return this.releaseState.getProgress(repositoryName, releaseVersion).keySet();
    }

    private boolean areUnreleasedPlatformsPresent(final List<PlatformName> platforms,
            final Set<PlatformName> releasedPlatforms) {
        return !releasedPlatforms.containsAll(platforms);
    }

    private Report releaseOnPlatform(final Repository repository, final PlatformName platformName,
            final Progress progress) {
        final var report = ReleaseReport.create(platformName);
        try {
            LOGGER.info(() -> "Releasing on " + platformName + " platform.");
            final String releaseOutput = getReleaseMaker(platformName).makeRelease(repository, progress);
            saveProgress(platformName, repository, releaseOutput);
            report.addSuccessfulResult("Release finished.");
            LOGGER.info(() -> "Release on platform " + platformName + " is finished!");
        } catch (final Exception exception) {
            LOGGER.info(() -> "Release on platform " + platformName + " has failed!");
            report.addFailedResult(ExceptionUtils.getStackTrace(exception));
        }
        return report;
    }

    private void saveProgress(final PlatformName platformName, final Repository repository,
            final String releaseOutput) {
        this.releaseState.saveProgress(repository.getName(), repository.getVersion(), platformName, releaseOutput);
    }

    private void prepareRepositoryForRelease(final Repository repository) {
        this.releaseManager.prepareForRelease(repository);
    }

    private void cleanRepositoryAfterRelease(final Repository repository) {
        this.releaseManager.cleanUpAfterRelease(repository);
    }

    private ReleaseMaker getReleaseMaker(final PlatformName platformName) {
        return this.releaseMakers.get(platformName);
    }
}