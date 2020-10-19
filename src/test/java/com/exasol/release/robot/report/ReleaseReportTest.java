package com.exasol.release.robot.report;

import static com.exasol.release.robot.Platform.PlatformName.GITHUB;
import static com.exasol.release.robot.Platform.PlatformName.MAVEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReleaseReportTest {
    private ReleaseReport report;

    @BeforeEach
    void beforeEach() {
        this.report = new ReleaseReport();
    }

    @Test
    void testGetFailures() {
        this.report.addSuccessfulRelease(GITHUB);
        this.report.addFailedRelease(MAVEN, "Just because");
        assertThat(this.report.getFailuresReport(), containsString("Fail.    MAVEN: Just because"));
    }

    @Test
    void testHasFailedValidations() {
        assertThat(this.report.hasFailures(), equalTo(false));
        this.report.addSuccessfulRelease(GITHUB);
        assertThat(this.report.hasFailures(), equalTo(false));
        this.report.addFailedRelease(MAVEN, "Just because");
        assertThat(this.report.hasFailures(), equalTo(true));
        this.report.addSuccessfulRelease(GITHUB);
        assertThat(this.report.hasFailures(), equalTo(true));
    }

    @Test
    void testGetFullReport() {
        this.report.addSuccessfulRelease(GITHUB);
        this.report.addFailedRelease(MAVEN, "Just because");
        assertAll(() -> assertThat(this.report.getFullReport(), containsString("Success. GITHUB")),
                () -> assertThat(this.report.getFullReport(), containsString("Fail.    MAVEN: Just because")));
    }

    @Test
    void testGetShortDescriptionOnFail() {
        this.report.addFailedRelease(MAVEN, "Just because");
        assertThat(this.report.getShortDescription(), containsString("Release Report: RELEASE FAILED!"));
    }

    @Test
    void testGetShortDescriptionOnSuccess() {
        this.report.addSuccessfulRelease(GITHUB);
        assertThat(this.report.getShortDescription(), containsString("Release Report: Release is successful!"));
    }
}