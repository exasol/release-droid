package com.exasol.releasedroid.usecases.logging;

import static com.exasol.releasedroid.usecases.PlatformName.GITHUB;
import static com.exasol.releasedroid.usecases.PlatformName.MAVEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.logging.ReportFormatter;
import com.exasol.releasedroid.usecases.report.ReleaseResult;
import com.exasol.releasedroid.usecases.report.Report;

class ReportFormatterTest {
    private ReportFormatter reportFormatter;

    @BeforeEach
    void beforeEach() {
        this.reportFormatter = new ReportFormatter();
    }

    @Test
    void testFormatHeaderWithNoFailures() {
        final Report report = Report.releaseReport();
        assertThat(this.reportFormatter.formatHeader(report), containsString("RELEASE Report: release is successful!"));
    }

    @Test
    void testFormatHeaderWithFailures() {
        final Report report = Report.releaseReport();
        report.addResult(ReleaseResult.failedRelease(MAVEN, "Just because"));
        assertThat(this.reportFormatter.formatHeader(report), containsString("RELEASE Report: RELEASE FAILED!"));
    }

    @Test
    void testFormatReport() {
        final Report report = Report.releaseReport();
        report.addResult(ReleaseResult.successfulRelease(GITHUB));
        report.addResult(ReleaseResult.failedRelease(MAVEN, "Just because"));
        final String formattedReport = this.reportFormatter.formatReport(report);
        assertAll(() -> assertThat(formattedReport, containsString("Success. GITHUB")),
                () -> assertThat(formattedReport, containsString("Fail.    MAVEN: Just because")));
    }

    @Test
    void testFormatReportWithFailuresOnly() {
        final Report report = Report.releaseReport();
        report.addResult(ReleaseResult.successfulRelease(GITHUB));
        report.addResult(ReleaseResult.failedRelease(MAVEN, "Just because"));
        final String formattedReport = this.reportFormatter.formatReportWithFailuresOnly(report);
        assertAll(() -> assertFalse(formattedReport.contains("Success. GITHUB")),
                () -> assertThat(formattedReport, containsString("Fail.    MAVEN: Just because")));
    }

}
