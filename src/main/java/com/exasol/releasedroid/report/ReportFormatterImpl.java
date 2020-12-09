package com.exasol.releasedroid.report;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.Result;

/**
 * Implementation of {@link ReportFormatter}.
 *
 */
public class ReportFormatterImpl implements ReportFormatter {

    /**
     * Get a formatted report as a string.
     *
     * @return report as a formatted string
     */
    @Override
    public String formatReport(final Report report) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.formatHeader(report));
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append(this.formatBody(report));
        return stringBuilder.toString();
    }

    /**
     * Get a formatted report as a string.
     *
     * @return report as a formatted string
     */
    @Override
    public String formatReportWithFailuresOnly(final Report report) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.formatHeader(report));
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append(this.formatFailures(report));
        return stringBuilder.toString();
    }

    /**
     * Get a formatted report as a string.
     *
     * @return report as a formatted string
     */
    private String formatBody(final Report report) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Result result : report.getResults()) {
            stringBuilder.append(result.toString());
            stringBuilder.append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    /**
     * Get a short report description.
     *
     * @return short description as a string
     */
    @Override
    public String formatHeader(final Report report) {
        final String header = report.getReportName() + " Report: ";
        if (report.hasFailures()) {
            return header + report.getReportName() + " FAILED!";
        } else {
            return header + report.getReportName().toString().toLowerCase() + " is successful!";
        }
    }

    /**
     * Get a failed validations report.
     *
     * @return report as a string
     */
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
