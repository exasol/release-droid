package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.exasol.releasedroid.usecases.UserInput;
import com.exasol.releasedroid.usecases.report.Report;

public class SummaryFormatter {
    private final ReportFormatter reportFormatter;

    public SummaryFormatter(final ReportFormatter reportFormatter) {
        this.reportFormatter = reportFormatter;
    }

    public String formatResponse(final UserInput userInput, final List<Report> reports) {
        return formatInputUser(userInput) + formatReports(reports);
    }

    private String formatInputUser(final UserInput userInput) {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(println(now));
        stringBuilder.append(println(""));
        stringBuilder.append(println("Goal: " + userInput.getGoal()));
        stringBuilder.append(println("Repository: " + userInput.getRepositoryName()));
        stringBuilder.append(println("Platforms: "
                + userInput.getPlatformNames().stream().map(Enum::name).collect(Collectors.joining(", "))));
        if (userInput.hasBranch()) {
            stringBuilder.append(println("Git branch: " + userInput.getBranch()));
        }
        stringBuilder.append(println(""));
        return stringBuilder.toString();
    }

    private String println(final String string) {
        return string + LINE_SEPARATOR;
    }

    private String formatReports(final List<Report> reports) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Report report : reports) {
            stringBuilder.append(this.reportFormatter.formatReport(report));
            stringBuilder.append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }
}
