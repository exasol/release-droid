package com.exasol.releasedroid.usecases.release;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;
import com.exasol.releasedroid.usecases.UseCase;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.report.ReleaseReport;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.request.ReleasePlatforms;

/**
 * Implements the Release use case.
 */
public class ReleaseInteractor implements UseCase {
    private static final Logger LOGGER = Logger.getLogger(ReleaseInteractor.class.getName());
    private final Map<PlatformName, ReleaseMaker> releaseMakers;
    private final ProgressRecorder progressRecorder;
    private final ReleaseManager releaseManager;
    private Path releaseGuidePath = null;

    /**
     * Create a new instance of {@link ReleaseInteractor}.
     *
     * @param releaseMakers  map with platform names and release makers
     * @param releaseManager instance of {@link ReleaseManager}
     * @param releaseState   release state to incrementally save the current state or progress enabling to recover and
     *                       continue after a failure
     */

    public ReleaseInteractor(final Map<PlatformName, ReleaseMaker> releaseMakers, final ReleaseManager releaseManager,
            final ProgressRecorder progressRecorder) {
        this.releaseMakers = releaseMakers;
        this.releaseManager = releaseManager;
        this.progressRecorder = progressRecorder;
    }

    @Override
    // [impl->dsn~rd-runs-release-goal~1]
    public List<Report> apply(final Repository repository, final ReleasePlatforms platforms) {
        try {
            return releaseOnPlatforms(repository, platforms);
        } catch (final Exception exception) {
            throw new ReleaseException(messageBuilder("E-RD-18").message("Error creating release").toString(),
                    exception);
        }
    }

    private List<Report> releaseOnPlatforms(final Repository repository, final ReleasePlatforms platforms) {
        final Iterator<PlatformName> it = platforms.list().iterator();
        if (!it.hasNext()) {
            return List.of();
        }
        final Progress progress = this.releaseManager.estimateDuration( //
                repository, estimateDuration(repository, platforms.list()));
        prepareRepositoryForRelease(repository);
        final ReleaseReport reports = ReleaseReport.create();

        boolean failure = false;
        while (!failure && it.hasNext()) {
            final PlatformName platform = it.next();
            final Report platformReport = releaseOnPlatform(repository, platform, progress);
            reports.merge(platformReport);
            failure = platformReport.hasFailures();
            createReleaseGuide(repository, progress.gitHubTagUrl(), platforms.releaseGuide());
        }
        progress.reportStatus().newline();
        if (!reports.hasFailures()) {
            cleanRepositoryAfterRelease(repository);
        }
        return List.of(reports);
    }

    /**
     * If user requested to generate release guide and HTML URL of GitHub tag is known already (as provided by
     * GitHubReleaseMaker) and guide has not been generated, yet, then generate release guide.
     *
     * @param repository   repository to retrieve additional information for release guide
     * @param gitHubTagUrl HTML URL of GitHub release, empty if GitHub tag has not been created, yet
     * @param path         optional path to release guide or empty if user did not request to generate such
     */
    private void createReleaseGuide(final Repository repository, final Optional<URL> gitHubTagUrl,
            final Optional<Path> path) {
        if (path.isEmpty()) {
            return; // user did not request to generate a release guide
        }
        if (gitHubTagUrl.isEmpty()) {
            return; // HTML URL of GitHub tag is not known, yet.
        }
        if (this.releaseGuidePath != null) {
            return; // release guide has already been generated, no need to generate it twice
        }
        this.releaseGuidePath = path.get();
        this.releaseManager.generateReleaseGuide(repository, gitHubTagUrl.get().toString(), this.releaseGuidePath);
    }

    // [impl->dsn~estimate-duration~1]
    private Estimation estimateDuration(final Repository repository, final List<PlatformName> platforms) {
        Estimation estimation = Estimation.empty();
        for (final PlatformName platform : platforms) {
            estimation = estimation.plus(getReleaseMaker(platform).estimateDuration(repository));
        }
        return estimation;
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
        this.progressRecorder.saveProgress(repository.getName(), repository.getVersion(), platformName, releaseOutput);
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
