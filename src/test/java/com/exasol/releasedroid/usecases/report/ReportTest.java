package com.exasol.releasedroid.usecases.report;

import static com.exasol.releasedroid.usecases.PlatformName.GITHUB;
import static com.exasol.releasedroid.usecases.PlatformName.MAVEN;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// [utest->dsn~rr-creates-release-report~1]
class ReportTest {
    private Report report;

    @BeforeEach
    void beforeEach() {
        this.report = Report.releaseReport();
    }

    @Test
    void testGetFailures() {
        this.report.addResult(ReleaseResult.successfulRelease(GITHUB));
        this.report.addResult(ReleaseResult.failedRelease(MAVEN, "Just because"));
        assertThat(this.report.toString(), containsString("Fail.    MAVEN: Just because"));
    }

    @Test
    void testHasFailedValidations() {
        assertFalse(this.report.hasFailures());
        this.report.addResult(ReleaseResult.successfulRelease(GITHUB));
        assertFalse(this.report.hasFailures());
        this.report.addResult(ReleaseResult.failedRelease(MAVEN, "Just because"));
        assertTrue(this.report.hasFailures());
        this.report.addResult(ReleaseResult.successfulRelease(GITHUB));
        assertTrue(this.report.hasFailures());
    }

}