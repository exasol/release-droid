package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.report.ReleaseReport;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.request.PlatformName;

class ReportSummaryFormatterTest {
    private final ReportSummaryFormatter formatter = new ReportSummaryFormatter();

    @Test
    void testFormatReleaseReport() {
        final Report outer = ReleaseReport.create();
        final Report github = ReleaseReport.create(PlatformName.GITHUB);
        github.addSuccessfulResult("Release finished.");
        final Report maven = ReleaseReport.create(PlatformName.MAVEN);
        maven.addSuccessfulResult("Release finished.");
        outer.merge(github);
        outer.merge(maven);
        final String formattedReport = this.formatter.formatReport(outer);
        assertThat(formattedReport, equalTo(//
                "Release is successful!" + LINE_SEPARATOR //
                        + LINE_SEPARATOR //
                        + "Release succeeded. [GITHUB,MAVEN] Release finished." + LINE_SEPARATOR //
        ));
    }

    @Test
    void testFormatReleaseReportWithFailures() {
        final Report outer = ReleaseReport.create();
        final Report github = ReleaseReport.create(PlatformName.GITHUB);
        github.addSuccessfulResult("Release finished.");
        final Report maven = ReleaseReport.create(PlatformName.MAVEN);
        maven.addFailedResult("Wrong credentials.");
        outer.merge(github);
        outer.merge(maven);
        final String formattedReport = this.formatter.formatReport(outer);
        assertThat(formattedReport, equalTo( //
                "RELEASE FAILED!" + LINE_SEPARATOR //
                        + LINE_SEPARATOR //
                        + "Release succeeded. [GITHUB] Release finished." + LINE_SEPARATOR //
                        + "Release failed. [MAVEN] Wrong credentials." + LINE_SEPARATOR //
        ));
    }

    @Test
    void testFormatValidationReport() {
        final Report outer = ValidationReport.create();
        final Report github = ValidationReport.create(PlatformName.GITHUB);
        github.addSuccessfulResult("Date is correct!");
        final Report maven = ValidationReport.create(PlatformName.MAVEN);
        maven.addSuccessfulResult("Plugins are here!");
        outer.merge(github);
        outer.merge(maven);
        final String formattedReport = this.formatter.formatReport(outer);
        assertThat(formattedReport, equalTo(//
                "Validation is successful!" + LINE_SEPARATOR //
                        + LINE_SEPARATOR //
                        + "Validation succeeded. [GITHUB] Date is correct!" + LINE_SEPARATOR //
                        + "Validation succeeded. [MAVEN] Plugins are here!" + LINE_SEPARATOR //
        ));
    }

    @Test
    void testFormatValidationReportWithFailures() {
        final Report outer = ValidationReport.create();
        final Report github = ValidationReport.create(PlatformName.GITHUB);
        github.addFailedResult("Date is wrong!");
        github.addSuccessfulResult("Changelog is fine!");
        final Report maven = ValidationReport.create(PlatformName.MAVEN);
        maven.addFailedResult("Date is wrong!");
        maven.addFailedResult("Some plugins are missing!");
        outer.merge(github);
        outer.merge(maven);
        final String formattedReport = this.formatter.formatReport(outer);
        assertThat(formattedReport, equalTo( //
                "VALIDATION FAILED!" + LINE_SEPARATOR //
                        + LINE_SEPARATOR //
                        + "Validation failed. [GITHUB,MAVEN] Date is wrong!" + LINE_SEPARATOR //
                        + "Validation succeeded. [GITHUB] Changelog is fine!" + LINE_SEPARATOR //
                        + "Validation failed. [MAVEN] Some plugins are missing!" + LINE_SEPARATOR //
        ));
    }
}