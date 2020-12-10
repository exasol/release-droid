package com.exasol.releasedroid.usecases.logging;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.Result;

/**
 * Formatter for {@link Report}.
 */
public class ReportFormatter {

    /**
     * Get a formatted report as a string.
     *
     * @return report as a formatted string
     */
    public String formatReport(final Report report) {
        return this.formatHeader(report) + LINE_SEPARATOR + this.formatBody(report);
    }

    /**
     * Get a formatted report with only its failed results as a string.
     *
     * @return report as a formatted string
     */
    public String formatReportWithFailuresOnly(final Report report) {
        return this.formatHeader(report) + LINE_SEPARATOR + this.formatFailures(report);
    }

    /**
     * Get a short report description.
     *
     * @return short description as a string
     */
    public String formatHeader(final Report report) {
        final String header = report.getReportName() + " Report: ";
        if (report.hasFailures()) {
            return header + report.getReportName() + " FAILED!";
        } else {
            return header + report.getReportName().toString().toLowerCase() + " is successful!";
        }
    }

    private String formatBody(final Report report) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Result result : report.getResults()) {
            stringBuilder.append(result.toString());
            stringBuilder.append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    private String formatFailures(final Report report) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LINE_SEPARATOR);
        for (final Result result : report.getResults()) {
            if (!result.isSuccessful()) {
                stringBuilder.append(result.toString());
                stringBuilder.append(LINE_SEPARATOR);
            }
        }
        return stringBuilder.toString();
    }

}
