package com.exasol.releasedroid.usecases.report;

import static com.exasol.releasedroid.usecases.request.PlatformName.GITHUB;
import static com.exasol.releasedroid.usecases.request.PlatformName.MAVEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.Test;

// [utest->dsn~rd-creates-release-report~1]
class ValidationReportTest {
    @Test
    void testValidationReport() {
        final ValidationReport report = ValidationReport.create();
        report.addSuccessfulResult("Validation is done!");
        report.addSuccessfulResult("Another validation is done!");
        final List<Result> resultList = report.getResults();
        assertAll(() -> assertThat(report.getReportName(), equalTo("Validation")), //
                () -> assertThat(report.hasFailures(), equalTo(false)), //
                () -> assertThat(resultList.size(), equalTo(2)), //
                () -> assertThat(resultList.get(0),
                        equalTo(new Result(true, "Validation succeeded.", "Validation is done!"))), //
                () -> assertThat(resultList.get(1),
                        equalTo(new Result(true, "Validation succeeded.", "Another validation is done!"))) //
        );
    }

    @Test
    void testValidationReportWithFailures() {
        final ValidationReport report = ValidationReport.create();
        report.addSuccessfulResult("Validation is done!");
        report.addFailedResult("You have some problems...");
        final List<Result> resultList = report.getResults();
        assertAll(() -> assertThat(report.getReportName(), equalTo("Validation")), //
                () -> assertThat(report.hasFailures(), equalTo(true)), //
                () -> assertThat(resultList.size(), equalTo(2)), //
                () -> assertThat(resultList.get(0),
                        equalTo(new Result(true, "Validation succeeded.", "Validation is done!"))), //
                () -> assertThat(resultList.get(1),
                        equalTo(new Result(false, "Validation failed.", "You have some problems..."))) //
        );
    }

    @Test
    void testMergeReportsWithPlatformNames() {
        final ValidationReport outer = ValidationReport.create();
        final ValidationReport githubReport = ValidationReport.create(GITHUB);
        final ValidationReport subGitHubReport = ValidationReport.create();
        subGitHubReport.addSuccessfulResult("pom.xml is fine.");
        subGitHubReport.addFailedResult("Wrong release date!");
        subGitHubReport.addSuccessfulResult("GitHub repository is ready.");
        githubReport.merge(subGitHubReport);
        final ValidationReport mavenReport = ValidationReport.create(MAVEN);
        final ValidationReport subMavenReport = ValidationReport.create();
        subMavenReport.addSuccessfulResult("pom.xml is fine.");
        subMavenReport.addFailedResult("Missing workflow!");
        subMavenReport.addFailedResult("Wrong release date!");
        mavenReport.merge(subMavenReport);
        outer.merge(githubReport);
        outer.merge(mavenReport);

        final List<Result> resultList = outer.getResults();
        final Result result1 = resultList.get(0);
        final Result result2 = resultList.get(1);
        final Result result3 = resultList.get(2);
        final Result result4 = resultList.get(3);
        assertAll(() -> assertThat(outer.getReportName(), equalTo("Validation")), //
                () -> assertThat(outer.hasFailures(), equalTo(true)), //
                () -> assertThat(resultList.size(), equalTo(4)), //
                () -> assertThat(result1, equalTo(new Result(true, "Validation succeeded.", "pom.xml is fine."))), //
                () -> assertThat(result1.getPlatformNames(), equalTo(List.of(GITHUB, MAVEN))), //
                () -> assertThat(result2, equalTo(new Result(false, "Validation failed.", "Wrong release date!"))), //
                () -> assertThat(result2.getPlatformNames(), equalTo(List.of(GITHUB, MAVEN))), //
                () -> assertThat(result3,
                        equalTo(new Result(true, "Validation succeeded.", "GitHub repository is ready."))), //
                () -> assertThat(result3.getPlatformNames(), equalTo(List.of(GITHUB))), //
                () -> assertThat(result4, equalTo(new Result(false, "Validation failed.", "Missing workflow!"))), //
                () -> assertThat(result4.getPlatformNames(), equalTo(List.of(MAVEN))) //
        );
    }
}