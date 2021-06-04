package com.exasol.releasedroid.usecases.report;

import java.util.List;

/**
 * Represents a report.
 */
public interface Report {
    /**
     * Check if report has failed results.
     *
     * @return true if one or more failed result exists
     */
    boolean hasFailures();

    /**
     * Merge two reports.
     *
     * @param report report to be merged
     */
    void merge(Report report);

    /**
     * Get a list of results.
     *
     * @return list of results
     */
    List<Result> getResults();

    /**
     * Get a report's name.
     *
     * @return report name
     */
    String getReportName();

    /**
     * Add a new successful result.
     *
     * @param message message
     */
    void addSuccessfulResult(String message);

    /**
     * Add a new failed result.
     *
     * @param message message
     */
    void addFailedResult(String message);
}