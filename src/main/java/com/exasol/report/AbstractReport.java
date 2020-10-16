package com.exasol.report;

import static com.exasol.ReleaseRobotConstants.LINE_SEPARATOR;

import java.util.LinkedList;
import java.util.List;

/**
 * An abstract base for a report.
 */
public abstract class AbstractReport implements Report {
    protected final List<Result> results = new LinkedList<>();
    protected boolean hasFailures;

    @Override
    public String getFullReport() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Result validationResult : this.results) {
            stringBuilder.append(validationResult.toString());
            stringBuilder.append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean hasFailures() {
        return this.hasFailures;
    }

    @Override
    public String getFailuresReport() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LINE_SEPARATOR);
        for (final Result result : this.results) {
            if (!result.isSuccessful()) {
                stringBuilder.append(result.toString());
                stringBuilder.append(LINE_SEPARATOR);
            }
        }
        return stringBuilder.toString();
    }

    protected String getShortDescription(String goal) {
        final String reportName = goal + " Report: ";
        if (this.hasFailures()) {
            return reportName + goal.toUpperCase() + " FAILED!";
        } else {
            return reportName + goal + " is successful!";
        }
    }
}