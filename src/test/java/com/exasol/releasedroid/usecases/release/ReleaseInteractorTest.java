package com.exasol.releasedroid.usecases.release;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;
import com.exasol.releasedroid.usecases.UseCase;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.*;

@ExtendWith(MockitoExtension.class)
class ReleaseInteractorTest {

    private static final String GITHUB_TAG_URL = "https://github.com/owner/full-name/relases/tag/1.2.3";
    private static final String REPOSITORY_NAME = "repo-name";
    private static final String REPOSITORY_VERSION = "repo-version";
    private static final String GITHUB_RELEASE_OUTPUT = "GITHUB_RELEASE_OUTPUT";

    @Mock
    private UseCase validateUseCaseMock;
    @Mock
    private ReleaseManager releaseManagerMock;
    @Mock
    private Repository repositoryMock;
    @Mock
    private ReleaseState releaseStateMock;
    @Mock
    private ReleaseMaker githubReleaseMakerMock;
    @Mock
    private ReleaseMaker jiraReleaseMakerMock;
    @Mock
    private ReleaseMaker mavenReleaseMakerMock;
    @Mock
    private ReleaseMaker communityReleaseMakerMock;

    private Progress progress;

    @BeforeEach
    void setup() {
        when(this.repositoryMock.getName()).thenReturn(REPOSITORY_NAME);
        when(this.repositoryMock.getVersion()).thenReturn(REPOSITORY_VERSION);
        // this.progress.setGitHubTagUrl(url(GITHUB_TAG_URL));
    }

    @Test
    void testReleaseNoPlatforms() {
        release(emptyList(), emptySet());
        verifyNoInteractions(this.releaseManagerMock, this.validateUseCaseMock);
    }

    @Test
    void testReleaseSinglePlatformSuccess() {
        simulateSuccessValidationReport(PlatformName.GITHUB);
        mockEstimationAndProgress(this.releaseManagerMock, this.githubReleaseMakerMock);
        when(this.githubReleaseMakerMock.makeRelease(this.repositoryMock, this.progress))
                .thenReturn(GITHUB_RELEASE_OUTPUT);
        final List<Report> reports = release(List.of(PlatformName.GITHUB), emptySet());
        assertReport(reports, ReportStatus.SUCCESS, ReportStatus.SUCCESS);
        verifyGithubReleaseAndCleanup();
    }

    private void assertReport(final List<Report> reports, final ReportStatus validationResult,
            final ReportStatus releaseResult) {
        assertAll(() -> assertThat(reports, hasSize(2)),
                () -> assertThat("Validation Report: " + reports.get(0), reports.get(0).hasFailures(),
                        is(validationResult.failure)),
                () -> assertThat("Release Report: " + reports.get(1), reports.get(1).hasFailures(),
                        is(releaseResult.failure)));
    }

    private enum ReportStatus {
        SUCCESS(false), FAILURE(true);

        private final boolean failure;

        private ReportStatus(final boolean failure) {
            this.failure = failure;
        }
    }

    private void verifyGithubReleaseAndCleanup() {
        final InOrder inOrder = inOrder(this.releaseManagerMock, this.githubReleaseMakerMock);
        inOrder.verify(this.githubReleaseMakerMock).estimateDuration(this.repositoryMock);
        inOrder.verify(this.releaseManagerMock).estimateDuration(eq(this.repositoryMock), any());
        inOrder.verify(this.releaseManagerMock).prepareForRelease(this.repositoryMock);
        inOrder.verify(this.githubReleaseMakerMock).makeRelease(this.repositoryMock, this.progress);
        inOrder.verify(this.releaseManagerMock).cleanUpAfterRelease(this.repositoryMock);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testReleaseSinglePlatformValidationFailure() {
        simulateFailureValidationReport(PlatformName.GITHUB);
        mockEstimationAndProgress(this.releaseManagerMock, this.githubReleaseMakerMock);
        final List<Report> reports = release(List.of(PlatformName.GITHUB), emptySet());
        assertReport(reports, ReportStatus.FAILURE, ReportStatus.SUCCESS);
        verifyGithubReleaseSkipped();
    }

    private void verifyGithubReleaseSkipped() {
        final InOrder inOrder = inOrder(this.releaseManagerMock);
        inOrder.verify(this.releaseManagerMock).estimateDuration(eq(this.repositoryMock), any());
        inOrder.verify(this.releaseManagerMock).prepareForRelease(this.repositoryMock);
        inOrder.verify(this.releaseManagerMock).cleanUpAfterRelease(this.repositoryMock);
        inOrder.verifyNoMoreInteractions();
        verify(this.githubReleaseMakerMock).estimateDuration(this.repositoryMock);
        verifyNoMoreInteractions(this.githubReleaseMakerMock);
    }

    @Test
    void testReleaseSinglePlatformReleaseFailure() {
        simulateSuccessValidationReport(PlatformName.GITHUB);
        simulateReleaseFailure(this.githubReleaseMakerMock);
        mockEstimationAndProgress(this.releaseManagerMock, this.githubReleaseMakerMock);
        final List<Report> reports = release(List.of(PlatformName.GITHUB), emptySet());
        assertReport(reports, ReportStatus.SUCCESS, ReportStatus.FAILURE);
        verify(this.releaseManagerMock, never()).cleanUpAfterRelease(this.repositoryMock);
    }

    private void simulateReleaseFailure(final ReleaseMaker releaseMakerMock) {
        when(releaseMakerMock.makeRelease(this.repositoryMock, null)).thenThrow(new RuntimeException("expected"));
    }

    @Test
    void testReleaseMultiplePlatformsSkipsAfterValidationFailure() {
        simulateFailureValidationReport(PlatformName.GITHUB);
        mockEstimationAndProgress(this.releaseManagerMock, this.githubReleaseMakerMock, this.mavenReleaseMakerMock);
        final List<Report> reports = release(List.of(PlatformName.GITHUB, PlatformName.MAVEN), emptySet());
        assertReport(reports, ReportStatus.FAILURE, ReportStatus.SUCCESS);
        verify(this.mavenReleaseMakerMock).estimateDuration(this.repositoryMock);
        verifyNoMoreInteractions(this.mavenReleaseMakerMock);
    }

    @Test
    void testReleaseMultiplePlatformsSkipsAfterReleaseFailure() {
        simulateSuccessValidationReport(PlatformName.GITHUB);
        simulateReleaseFailure(this.githubReleaseMakerMock);
        mockEstimationAndProgress(this.releaseManagerMock, this.githubReleaseMakerMock, this.mavenReleaseMakerMock);
        final List<Report> reports = release(List.of(PlatformName.GITHUB, PlatformName.MAVEN), emptySet());
        assertReport(reports, ReportStatus.SUCCESS, ReportStatus.FAILURE);
        verify(this.mavenReleaseMakerMock).estimateDuration(this.repositoryMock);
        verifyNoMoreInteractions(this.mavenReleaseMakerMock);
    }

    private void simulateSuccessValidationReport(final PlatformName platform) {
        final Report report = ValidationReport.create(platform);
        report.addSuccessfulResult("success");
        simulateValidationReport(platform, report);
    }

    private void simulateFailureValidationReport(final PlatformName platform) {
        final Report report = ValidationReport.create(platform);
        report.addFailedResult("failure");
        simulateValidationReport(platform, report);
    }

    private void simulateValidationReport(final PlatformName platform, final Report report) {
        when(this.validateUseCaseMock.apply(same(this.repositoryMock), ArgumentMatchers.any()))
                .thenReturn(List.of(report));
    }

    @Test
    void testReleasePlatformSkippedWhenAlreadyReleased() {
        simulateStatusAlreadyReleased(PlatformName.GITHUB);
        final List<Report> reports = release(List.of(PlatformName.GITHUB), emptySet());
        assertThat(reports, emptyCollectionOf(Report.class));
        verifyNoInteractions(this.githubReleaseMakerMock);
    }

    @Test
    void testReleaseFails() {
        when(this.releaseStateMock.getProgress(REPOSITORY_NAME, REPOSITORY_VERSION))
                .thenThrow(new RuntimeException("expected"));
        final ReleaseException exception = assertThrows(ReleaseException.class, () -> release(PlatformName.GITHUB));
        assertAll( //
                () -> assertThat(exception.getMessage(), equalTo("E-RD-18: Error creating release")),
                () -> assertThat(exception.getCause().getMessage(), equalTo("expected")));
    }

    @Test
    void releaseGuide_Created() {
        final Path path = Path.of("/path/to/release-guide.html");
        simulateReleaseGuide(GITHUB_TAG_URL, path);
        verify(this.releaseManagerMock).generateReleaseGuide(this.repositoryMock, GITHUB_TAG_URL, path);
    }

    @Test
    void releaseGuide_NotRequested() {
        simulateReleaseGuide(GITHUB_TAG_URL, null);
        verify(this.releaseManagerMock, never()).generateReleaseGuide(any(), any(), any());
    }

    @Test
    void releaseGuide_NoGitHubTagYet() {
        simulateReleaseGuide(null, Path.of("/path/to/release-guide.html"));
        verify(this.releaseManagerMock, never()).generateReleaseGuide(any(), any(), any());
    }

    private void simulateReleaseGuide(final String gitHubTagUrl, final Path path) {
        mockEstimationAndProgress(this.releaseManagerMock, this.githubReleaseMakerMock, this.mavenReleaseMakerMock);
        if (gitHubTagUrl != null) {
            this.progress.setGitHubTagUrl(url(gitHubTagUrl));
        }
        release(List.of(PlatformName.GITHUB, PlatformName.MAVEN), emptySet(), path);
    }

    // --------------------------------------------------

    private List<Report> release(final PlatformName... platforms) {
        return release(asList(platforms), emptySet());
    }

    private List<Report> release(final List<PlatformName> platformNames, final Set<PlatformName> skip) {
        return release(platformNames, skip, null);
    }

    private List<Report> release(final List<PlatformName> platformNames, final Set<PlatformName> skip,
            final Path releaseGuide) {
        final ReleasePlatforms platforms = new ReleasePlatforms(Goal.RELEASE, platformNames, skip,
                Optional.ofNullable(releaseGuide));
        final Map<PlatformName, ReleaseMaker> releaseMakers = Map.of( //
                PlatformName.GITHUB, this.githubReleaseMakerMock, //
                PlatformName.JIRA, this.jiraReleaseMakerMock, //
                PlatformName.MAVEN, this.mavenReleaseMakerMock, //
                PlatformName.COMMUNITY, this.communityReleaseMakerMock);
        final ReleaseInteractor releaseInteractor = new ReleaseInteractor(this.validateUseCaseMock, releaseMakers,
                this.releaseManagerMock, this.releaseStateMock);
        return releaseInteractor.apply(this.repositoryMock, platforms);
    }

    private void mockEstimationAndProgress(final ReleaseManager manager, final ReleaseMaker... releaseMakers) {
        this.progress = Progress.silent();
        when(manager.estimateDuration(any(), any())).thenReturn(this.progress);
        for (final ReleaseMaker maker : releaseMakers) {
            when(maker.estimateDuration(any())).thenReturn(Estimation.of(Duration.ofSeconds(1)));
        }
    }

    private URL url(final String url) {
        try {
            return new URL(GITHUB_TAG_URL);
        } catch (final MalformedURLException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private void simulateStatusAlreadyReleased(final PlatformName platform) {
        when(this.releaseStateMock.getProgress(REPOSITORY_NAME, REPOSITORY_VERSION))
                .thenReturn(Map.of(platform, "already released"));
    }
}
