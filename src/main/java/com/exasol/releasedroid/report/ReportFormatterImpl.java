package com.exasol.releasedroid.report;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import com.exasol.releasedroid.usecases.report.*;

/**
 * Implementation of {@link ReportFormatter}.
 */
public class ReportFormatterImpl implements ReportFormatter {

    @Override
    public String formatReport(final Report report) {
        return this.formatHeader(report) + LINE_SEPARATOR + this.formatBody(report);
    }

    @Override
    public String formatReportWithFailuresOnly(final Report report) {
        return this.formatHeader(report) + LINE_SEPARATOR + this.formatFailures(report);
    }

    private String formatBody(final Report report) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Result result : report.getResults()) {
            stringBuilder.append(result.toString());
            stringBuilder.append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    @Override
    public String formatHeader(final Report report) {
        final String header = report.getReportName() + " Report: ";
        if (report.hasFailures()) {
            return header + report.getReportName() + " FAILED!";
        } else {
            return header + report.getReportName().toString().toLowerCase() + " is successful!";
        }
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