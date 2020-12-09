package com.exasol.releasedroid.report;

import com.exasol.releasedroid.usecases.report.Report;

/**
 * Formatter for reports.
 *
 */
public interface ReportFormatter {
    /**
     * Get a formatted report as a string.
     *
     * @return report as a formatted string
     */
    public String formatReport(final Report report);

    /**
     * Get a formatted report with only its failed results as a string.
     *
     * @return report as a formatted string
     */
    public String formatReportWithFailuresOnly(final Report report);

    /**
     * Get a short report description.
     *
     * @return short description as a string
     */
    public String formatHeader(final Report report);
}
