package com.exasol.releasedroid.output;

import com.exasol.releasedroid.usecases.report.Report;

/**
 * Formatter for {@link Report}.
 */
public interface ReportFormatter {
    /**
     * Get a formatted report as a string.
     *
     * @return formatted report as a string
     */
    String formatReport(Report report);
}
