package com.exasol.report;

/**
 * Represents a report.
 */
public interface Report {
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
    boolean hasFailures();

    /**
     * Get a failed validations report.
     *
     * @return report as a string
     */
    String getFailuresReport();
}