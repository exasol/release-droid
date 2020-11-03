package com.exasol.releaserobot.usecases;

import java.util.List;

import com.exasol.releaserobot.usecases.ReportImpl.ReportName;

/**
 * Represents a report.
 */
public interface Report {
    /**
     * Add a new result.
     *
     * @param result result
     */
    public void addResult(Result result);

    /**
     * Get a formatted report as a string.
     *
     * @return report as a formatted string
     */
    public String getFullReport();

    /**
     * Get a short report description.
     *
     * @return short description as a string
     */
    public String getShortDescription();

    /**
     * Check if report has failed results.
     *
     * @return true if one or more failed result exists
     */
    public boolean hasFailures();

    /**
     * Get a failed validations report.
     *
     * @return report as a string
     */
    public String getFailuresReport();

    /**
     * Merge two reports.
     *
     * @param report report to be merged
     */
    public void merge(Report report);

    /**
     * Get a report's name.
     *
     * @return report name
     */
    public ReportName getReportName();

    /**
     * Get a list of results.
     *
     * @return list of results
     */
    public List<Result> getResults();
}