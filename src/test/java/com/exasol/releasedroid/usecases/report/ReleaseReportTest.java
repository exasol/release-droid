package com.exasol.releasedroid.usecases.report;

import static com.exasol.releasedroid.usecases.request.PlatformName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

// [utest->dsn~rd-creates-release-report~1]
class ReleaseReportTest {
    @Test
    void testReleaseReport() {
        final ReleaseReport report = ReleaseReport.create();
        report.addSuccessfulResult("Release is done!");
        report.addSuccessfulResult("Another release is done!");
        final List<Result> resultList = report.getResults();
        assertAll(() -> assertThat(report.getReportName(), equalTo("Release")), //
                () -> assertThat(report.hasFailures(), equalTo(false)), //
                () -> assertThat(resultList.size(), equalTo(2)), //
                () -> assertThat(resultList.get(0),
                        equalTo(new Result(true, "Release succeeded.", "Release is done!"))), //
                () -> assertThat(resultList.get(1),
                        equalTo(new Result(true, "Release succeeded.", "Another release is done!"))) //
        );
    }

    @Test
    void testReleaseReportWithFailures() {
        final ReleaseReport report = ReleaseReport.create();
        report.addSuccessfulResult("Release is done!");
        report.addFailedResult("Something is wrong...");
        final List<Result> resultList = report.getResults();
        assertAll(() -> assertThat(report.getReportName(), equalTo("Release")), //
                () -> assertThat(report.hasFailures(), equalTo(true)), //
                () -> assertThat(resultList.size(), equalTo(2)), //
                () -> assertThat(resultList.get(0),
                        equalTo(new Result(true, "Release succeeded.", "Release is done!"))), //
                () -> assertThat(resultList.get(1),
                        equalTo(new Result(false, "Release failed.", "Something is wrong..."))) //
        );
    }

    @Test
    void testFailedResultsDetected() {
        final ReleaseReport report = ReleaseReport.create();
        assertFalse(report.hasFailures());
        report.addSuccessfulResult("Release is done!");
        assertFalse(report.hasFailures());
        report.addFailedResult("Something is wrong...");
        assertTrue(report.hasFailures());
        report.addSuccessfulResult("Another release is done!");
        assertTrue(report.hasFailures());
    }

    @Test
    void testMergeReports() {
        final ReleaseReport first = ReleaseReport.create();
        first.addSuccessfulResult("Release is done!");
        final ReleaseReport second = ReleaseReport.create();
        second.addSuccessfulResult("Another release is done!");
        first.merge(second);
        final List<Result> resultList = first.getResults();
        assertAll(() -> assertThat(first.getReportName(), equalTo("Release")), //
                () -> assertThat(first.hasFailures(), equalTo(false)), //
                () -> assertThat(resultList.size(), equalTo(2)), //
                () -> assertThat(resultList.get(0),
                        equalTo(new Result(true, "Release succeeded.", "Release is done!"))), //
                () -> assertThat(resultList.get(1),
                        equalTo(new Result(true, "Release succeeded.", "Another release is done!"))) //
        );
    }

    @Test
    void testMergeReportsOppositeOrder() {
        final ReleaseReport first = ReleaseReport.create();
        first.addSuccessfulResult("Release is done!");
        final ReleaseReport second = ReleaseReport.create();
        second.addSuccessfulResult("Another release is done!");
        second.merge(first);
        final List<Result> resultList = second.getResults();
        assertAll(() -> assertThat(second.getReportName(), equalTo("Release")), //
                () -> assertThat(second.hasFailures(), equalTo(false)), //
                () -> assertThat(resultList.size(), equalTo(2)), //
                () -> assertThat(resultList.get(0),
                        equalTo(new Result(true, "Release succeeded.", "Another release is done!"))), //
                () -> assertThat(resultList.get(1), equalTo(new Result(true, "Release succeeded.", "Release is done!"))) //
        );
    }

    @Test
    void testMergeReportsWithPlatformNames() {
        final ReleaseReport first = ReleaseReport.create();
        final ReleaseReport second = ReleaseReport.create(GITHUB);
        second.addSuccessfulResult("Release is done!");
        final ReleaseReport third = ReleaseReport.create(MAVEN);
        third.addSuccessfulResult("Another release is done!");
        final ReleaseReport forth = ReleaseReport.create(COMMUNITY);
        forth.addFailedResult("Something went wrong...");
        first.merge(second);
        first.merge(third);
        first.merge(forth);
        final List<Result> resultList = first.getResults();
        final Result result1 = resultList.get(0);
        final Result result2 = resultList.get(1);
        final Result result3 = resultList.get(2);
        assertAll(() -> assertThat(first.getReportName(), equalTo("Release")), //
                () -> assertThat(first.hasFailures(), equalTo(true)), //
                () -> assertThat(resultList.size(), equalTo(3)), //
                () -> assertThat(result1, equalTo(new Result(true, "Release succeeded.", "Release is done!"))), //
                () -> assertThat(result1.getPlatformNames(), equalTo(List.of(GITHUB))), //
                () -> assertThat(result2, equalTo(new Result(true, "Release succeeded.", "Another release is done!"))), //
                () -> assertThat(result2.getPlatformNames(), equalTo(List.of(MAVEN))), //
                () -> assertThat(result3, equalTo(new Result(false, "Release failed.", "Something went wrong..."))), //
                () -> assertThat(result3.getPlatformNames(), equalTo(List.of(COMMUNITY))) //
        );
    }
}