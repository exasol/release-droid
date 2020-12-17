package com.exasol.releasedroid.usecases.report;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a report.
 */
public class Report {
    private final List<Result> results = new LinkedList<>();
    private final ReportName reportName;

    private Report(final ReportName reportName) {
        this.reportName = reportName;
    }

    /**
     * Create a new report with the name VALIDATION.
     *
     * @return new report
     */
    public static Report validationReport() {
        return new Report(ReportName.VALIDATION);
    }

    /**
     * Create a new report with the name RELEASE.
     *
     * @return new report
     */
    public static Report releaseReport() {
        return new Report(ReportName.RELEASE);
    }

    /**
     * Add a new result.
     *
     * @param result result
     */
    public void addResult(final Result result) {
        this.results.add(result);
    }

    /**
     * Check if report has failed results.
     *
     * @return true if one or more failed result exists
     */
    public boolean hasFailures() {
        for (final Result result : this.results) {
            if (!result.isSuccessful()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Merge two reports.
     *
     * @param report report to be merged
     */
    public void merge(final Report report) {
        this.results.addAll(report.getResults());
    }

    /**
     * Get a list of results.
     *
     * @return list of results
     */
    public List<Result> getResults() {
        return this.results;
    }

    /**
     * Get a report's name.
     *
     * @return report name
     */
    public ReportName getReportName() {
        return this.reportName;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Result result : this.results) {
            stringBuilder.append(result.toString());
        }
        return stringBuilder.toString();
    }

    /**
     * Available report names.
     */
    public enum ReportName {
        VALIDATION, RELEASE
    }
}