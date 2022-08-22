package com.exasol.releasedroid.formatting;

import com.exasol.releasedroid.usecases.report.Report;

/**
 * Formatter for {@link Report}.
 */
public interface ReportFormatter {
    /**
     * @param report report to format
     * @return formatted report as a string
     */
    String formatReport(Report report);
}
