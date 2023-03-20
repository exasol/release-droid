package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.formatting.Colorizer.green;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.util.Locale;
import java.util.stream.Collectors;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.Result;

/**
 * Formatting report for the logger output.
 */
public class ReportLogFormatter implements ReportFormatter {

    @Override
    public String formatReport(final Report report) {
        if (report.hasFailures()) {
            return formatReportWithFailures(report);
        } else {
            return formatSuccessMessage(report);
        }
    }

    private String formatReportWithFailures(final Report report) {
        return report.getReportName().toUpperCase(Locale.ROOT) + " FAILED!" //
                + LINE_SEPARATOR //
                + formatFailures(report);
    }

    private String formatFailures(final Report report) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(LINE_SEPARATOR);
        for (final Result result : report.getResults()) {
            if (result.isFailed()) {
                stringBuilder.append(formatFailedResult(result));
                stringBuilder.append(LINE_SEPARATOR);
            }
        }
        return stringBuilder.toString();
    }

    private String formatSuccessMessage(final Report report) {
        return green(report.getReportName() + " was performed without any problems.");
    }

    private String formatFailedResult(final Result result) {
        return result.getMessage() + " [For platforms: "
                + result.getPlatformNames().stream().map(Enum::name).collect(Collectors.joining(",")) + "]";
    }
}