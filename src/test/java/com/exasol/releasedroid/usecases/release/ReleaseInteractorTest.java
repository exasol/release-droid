package com.exasol.releasedroid.usecases.release;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ValidateUseCase;

@ExtendWith(MockitoExtension.class)
class ReleaseInteractorTest {
    private static final String REPOSITORY_NAME = "repo-name";
    private static final String REPOSITORY_VERSION = "repo-version";
    private static final String GITHUB_RELEASE_OUTPUT = "GITHUB_RELEASE_OUTPUT";

    @Mock
    private ValidateUseCase validateUseCaseMock;
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

    @BeforeEach
    void setup() {
        when(this.repositoryMock.getName()).thenReturn(REPOSITORY_NAME);
        when(this.repositoryMock.getVersion()).thenReturn(REPOSITORY_VERSION);
    }

    private List<Report> release(final PlatformName... platforms) {
        return release(asList(platforms), emptySet());
    }

    private List<Report> release(final List<PlatformName> platforms, final Set<PlatformName> skipValidationOn) {
        final Map<PlatformName, ReleaseMaker> releaseMarkers = Map.of( //
                PlatformName.GITHUB, this.githubReleaseMakerMock, //
                PlatformName.JIRA, this.jiraReleaseMakerMock, //
                PlatformName.MAVEN, this.mavenReleaseMakerMock, //
                PlatformName.COMMUNITY, this.communityReleaseMakerMock);
        final ReleaseInteractor releaseInteractor = new ReleaseInteractor(this.validateUseCaseMock, releaseMarkers,
                this.releaseManagerMock, this.releaseStateMock);
        return releaseInteractor.release(this.repositoryMock, platforms, skipValidationOn);
    }

    @Test
    void testReleaseNoPlatforms() {
        release(emptyList(), emptySet());
        verifyNoInteractions(this.releaseManagerMock, this.validateUseCaseMock);
    }

    @Test
    void testReleaseSinglePlatformSuccess() {
        simulateSuccessValidationReport(PlatformName.GITHUB);
        when(this.githubReleaseMakerMock.makeRelease(this.repositoryMock)).thenReturn(GITHUB_RELEASE_OUTPUT);
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
        inOrder.verify(this.releaseManagerMock).prepareForRelease(this.repositoryMock);
        inOrder.verify(this.githubReleaseMakerMock).makeRelease(this.repositoryMock);
        inOrder.verify(this.releaseManagerMock).cleanUpAfterRelease(this.repositoryMock);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testReleaseSinglePlatformValidationFailure() {
        simulateFailureValidationReport(PlatformName.GITHUB);
        final List<Report> reports = release(List.of(PlatformName.GITHUB), emptySet());
        assertReport(reports, ReportStatus.FAILURE, ReportStatus.SUCCESS);
        verifyGithubReleaseSkipped();
    }

    private void verifyGithubReleaseSkipped() {
        final InOrder inOrder = inOrder(this.releaseManagerMock);
        inOrder.verify(this.releaseManagerMock).prepareForRelease(this.repositoryMock);
        inOrder.verify(this.releaseManagerMock).cleanUpAfterRelease(this.repositoryMock);
        inOrder.verifyNoMoreInteractions();
        verifyNoInteractions(this.githubReleaseMakerMock);
    }

    @Test
    void testReleaseSinglePlatformReleaseFailure() {
        simulateSuccessValidationReport(PlatformName.GITHUB);
        simulateReleaseFailure(this.githubReleaseMakerMock);
        final List<Report> reports = release(List.of(PlatformName.GITHUB), emptySet());
        assertReport(reports, ReportStatus.SUCCESS, ReportStatus.FAILURE);
        verify(this.releaseManagerMock, never()).cleanUpAfterRelease(this.repositoryMock);
    }

    private void simulateReleaseFailure(final ReleaseMaker releaseMakerMock) {
        when(releaseMakerMock.makeRelease(this.repositoryMock)).thenThrow(new RuntimeException("expected"));
    }

    @Test
    void testReleaseMultiplePlatformsSkipsAfterValidationFailure() {
        simulateFailureValidationReport(PlatformName.GITHUB);
        final List<Report> reports = release(List.of(PlatformName.GITHUB, PlatformName.JIRA), emptySet());
        assertReport(reports, ReportStatus.FAILURE, ReportStatus.SUCCESS);
        verifyNoInteractions(this.jiraReleaseMakerMock);
    }

    @Test
    void testReleaseMultiplePlatformsSkipsAfterReleaseFailure() {
        simulateSuccessValidationReport(PlatformName.GITHUB);
        simulateReleaseFailure(this.githubReleaseMakerMock);
        final List<Report> reports = release(List.of(PlatformName.GITHUB, PlatformName.JIRA), emptySet());
        assertReport(reports, ReportStatus.SUCCESS, ReportStatus.FAILURE);
        verifyNoInteractions(this.jiraReleaseMakerMock);
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
        when(this.validateUseCaseMock.validate(same(this.repositoryMock), eq(List.of(platform)), eq(emptySet())))
                .thenReturn(report);
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
        assertThat(exception.getMessage(), equalTo("E-RD-18: Error creating release"));
        assertThat(exception.getCause().getMessage(), equalTo("expected"));
    }

    private void simulateStatusAlreadyReleased(final PlatformName platform) {
        when(this.releaseStateMock.getProgress(REPOSITORY_NAME, REPOSITORY_VERSION))
                .thenReturn(Map.of(platform, "already released"));
    }
}
