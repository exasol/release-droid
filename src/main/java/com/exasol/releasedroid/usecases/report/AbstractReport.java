package com.exasol.releasedroid.usecases.report;

import java.util.LinkedList;
import java.util.List;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * Abstract part of the report.
 */
public abstract class AbstractReport implements Report {
    private final List<Result> results = new LinkedList<>();
    private final String reportName;
    private final PlatformName platformName;

    protected AbstractReport(final String reportName, final PlatformName platformName) {
        this.reportName = reportName;
        this.platformName = platformName;
    }

    @Override
    public boolean hasFailures() {
        for (final Result result : this.results) {
            if (result.isFailed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void merge(final Report report) {
        if (!report.getReportName().equals(getReportName())) {
            throw new IllegalArgumentException(ExaError.messageBuilder("F-RD-15")
                    .message("Cannot merge two reports of different types: {{first}}, {{second}}.",
                            report.getReportName(), getReportName())
                    .ticketMitigation().toString());
        }
        for (final Result result : report.getResults()) {
            addResult(result);
        }
    }

    @Override
    public void addSuccessfulResult(final String message) {
        addResult(new Result(true, this.reportName + " succeeded.", message));
    }

    @Override
    public void addFailedResult(final String message) {
        addResult(new Result(false, this.reportName + " failed.", message));
    }

    @Override
    public List<Result> getResults() {
        return this.results;
    }

    @Override
    public String getReportName() {
        return this.reportName;
    }

    @Override
    public String toString() {
        return "AbstractReport{" + "results=" + this.results + ", reportName=" + this.reportName + '}';
    }

    private void addResult(final Result newResult) {
        if (this.platformName != null) {
            newResult.addPlatforms(List.of(this.platformName));
        }
        for (final Result result : this.results) {
            if (result.equals(newResult)) {
                result.addPlatforms(newResult.getPlatformNames());
                return;
            }
        }
        this.results.add(newResult);
    }
}