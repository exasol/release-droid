package com.exasol.releaserobot.report;

import static com.exasol.releaserobot.PlatformName.GITHUB;
import static com.exasol.releaserobot.PlatformName.MAVEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// [utest->dsn~rr-creates-release-report~1]
class ReportTest {
    private Report report;

    @BeforeEach
    void beforeEach() {
        this.report = new ReportImpl(ReportImpl.ReportName.RELEASE);
    }

    @Test
    void testGetFailures() {
        this.report.addResult(ReleaseResult.successfulRelease(GITHUB));
        this.report.addResult(ReleaseResult.failedRelease(MAVEN, "Just because"));
        assertThat(this.report.getFailuresReport(), containsString("Fail.    MAVEN: Just because"));
    }

    @Test
    void testHasFailedValidations() {
        assertThat(this.report.hasFailures(), equalTo(false));
        this.report.addResult(ReleaseResult.successfulRelease(GITHUB));
        assertThat(this.report.hasFailures(), equalTo(false));
        this.report.addResult(ReleaseResult.failedRelease(MAVEN, "Just because"));
        assertThat(this.report.hasFailures(), equalTo(true));
        this.report.addResult(ReleaseResult.successfulRelease(GITHUB));
        assertThat(this.report.hasFailures(), equalTo(true));
    }

    @Test
    void testGetFullReport() {
        this.report.addResult(ReleaseResult.successfulRelease(GITHUB));
        this.report.addResult(ReleaseResult.failedRelease(MAVEN, "Just because"));
        assertAll(() -> assertThat(this.report.getFullReport(), containsString("Success. GITHUB")),
                () -> assertThat(this.report.getFullReport(), containsString("Fail.    MAVEN: Just because")));
    }

    @Test
    void testGetShortDescriptionOnFail() {
        this.report.addResult(ReleaseResult.failedRelease(MAVEN, "Just because"));
        assertThat(this.report.getShortDescription(), containsString("RELEASE Report: RELEASE FAILED!"));
    }

    @Test
    void testGetShortDescriptionOnSuccess() {
        this.report.addResult(ReleaseResult.successfulRelease(GITHUB));
        assertThat(this.report.getShortDescription(), containsString("RELEASE Report: release is successful!"));
    }
}