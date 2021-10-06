package com.exasol.releasedroid.usecases.release;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_STATE_DIRECTORY;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.report.ReleaseReport;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
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
    public List<Report> release(final Repository repository, final List<PlatformName> platforms,
            final Set<PlatformName> skipValidationOn) {
        try {
            return makeRelease(repository, platforms, skipValidationOn);
        } catch (final Exception exception) {
            throw new ReleaseException(exception);
        }
    }

    private List<Report> makeRelease(final Repository repository, final List<PlatformName> platforms,
            final Set<PlatformName> skipValidationOn) {
        final Set<PlatformName> releasedPlatforms = getAlreadyReleasedPlatforms(repository.getName(),
                repository.getVersion());
        if (areUnreleasedPlatformsPresent(platforms, releasedPlatforms)) {
            final List<PlatformName> unreleasedPlatforms = getUnreleasedPlatforms(platforms, releasedPlatforms);
            return releaseOnPlatforms(repository, unreleasedPlatforms, skipValidationOn);
        } else {
            LOGGER.info(() -> "Nothing to release. The release has been already performed on all mentioned platforms.");
            return Collections.emptyList();
        }
    }

    @java.lang.SuppressWarnings("java:S135") // There is no a good workaround to avoid the second break here
    private List<Report> releaseOnPlatforms(final Repository repository, final List<PlatformName> platforms,
            final Set<PlatformName> skipValidationOn) {
        prepareRepositoryForRelease(repository);
        final var validationReport = ValidationReport.create();
        final var releaseReport = ReleaseReport.create();
        for (final PlatformName platform : platforms) {
            final var platformValidationReport = this.validateUseCase.validate(repository, List.of(platform),
                    skipValidationOn);
            validationReport.merge(platformValidationReport);
            if (!platformValidationReport.hasFailures()) {
                final var releaseReportForPlatform = releaseOnPlatform(repository, platform);
                releaseReport.merge(releaseReportForPlatform);
                if (releaseReportForPlatform.hasFailures()) {
                    break;
                }
            } else {
                LOGGER.warning(
                        () -> "Validation for a platform " + platform.name() + " failed. Release is interrupted.");
                break;
            }
        }
        if (!releaseReport.hasFailures()) {
            cleanRepositoryAfterRelease(repository);
        }
        return List.of(validationReport, releaseReport);
    }

    private List<PlatformName> getUnreleasedPlatforms(final List<PlatformName> platforms,
            final Set<PlatformName> releasedPlatforms) {
        final List<PlatformName> unreleasedPlatforms = new ArrayList<>();
        for (final PlatformName platform : platforms) {
            if (releasedPlatforms.contains(platform)) {
                LOGGER.info(() -> "Skipping " + platform + " platform, the release has been already performed there.");
            } else {
                unreleasedPlatforms.add(platform);
            }
        }
        return unreleasedPlatforms;
    }

    private Set<PlatformName> getAlreadyReleasedPlatforms(final String repositoryName, final String releaseVersion) {
        return this.releaseState.getProgress(repositoryName, releaseVersion).keySet();
    }

    private boolean areUnreleasedPlatformsPresent(final List<PlatformName> platforms,
            final Set<PlatformName> releasedPlatforms) {
        return !releasedPlatforms.containsAll(platforms);
    }

    private Report releaseOnPlatform(final Repository repository, final PlatformName platformName) {
        final var report = ReleaseReport.create(platformName);
        try {
            LOGGER.info(() -> "Releasing on " + platformName + " platform.");
            final String releaseOutput = getReleaseMaker(platformName).makeRelease(repository);
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