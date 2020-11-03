package com.exasol.releaserobot.report;

import static com.exasol.releaserobot.ReleaseRobotConstants.LINE_SEPARATOR;

import java.util.LinkedList;
import java.util.List;

/**
 * An abstract base for a report.
 */
public class ReportImpl implements Report {
    protected final List<Result> results = new LinkedList<>();
    private final ReportName reportName;

    public ReportImpl(final ReportName reportName) {
        this.reportName = reportName;
    }

    @Override
    public void addResult(final Result result) {
        this.results.add(result);
    }

    @Override
    public List<Result> getResults() {
        return this.results;
    }

    @Override
    public void addResults(final List<? extends Result> results) {
        this.results.addAll(results);
    }

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
        for (final Result result : this.results) {
            if (!result.isSuccessful()) {
                return true;
            }
        }
        return false;
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

    @Override
    public void merge(final Report report) {
        if (getReportName() != report.getReportName()) {
            throw new IllegalStateException("F-REP-1: Unable to merge two reports.");
        }
        addResults(report.getResults());
    }

    @Override
    public ReportName getReportName() {
        return this.reportName;
    }

    @Override
    public String getShortDescription() {
        final String header = this.reportName.name() + " Report: ";
        if (this.hasFailures()) {
            return header + this.reportName.name() + " FAILED!";
        } else {
            return header + this.reportName.name().toLowerCase() + " is successful!";
        }
    }

    public enum ReportName {
        VALIDATION, RELEASE
    }
}