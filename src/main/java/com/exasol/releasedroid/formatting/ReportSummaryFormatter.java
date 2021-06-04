package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.util.Locale;
import java.util.stream.Collectors;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.Result;

public class ReportSummaryFormatter implements ReportFormatter {
    @Override
    public String formatReport(final Report report) {
        return this.formatHeader(report) + println("") + this.formatBody(report);
    }

    private String formatHeader(final Report report) {
        if (report.hasFailures()) {
            return println(report.getReportName().toUpperCase(Locale.ROOT) + " FAILED!");
        } else {
            return println(report.getReportName() + " is successful!");
        }
    }

    private String println(final String string) {
        return string + LINE_SEPARATOR;
    }

    private String formatBody(final Report report) {
        final var stringBuilder = new StringBuilder();
        for (final Result result : report.getResults()) {
            final String platforms = result.getPlatformNames().stream().map(Enum::name)
                    .collect(Collectors.joining(","));
            final String resultLine = result.getActionDescription() + " [" + platforms + "] " + result.getMessage();
            stringBuilder.append(println(resultLine));
        }
        return stringBuilder.toString();
    }
}
