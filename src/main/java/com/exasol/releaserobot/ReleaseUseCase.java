package com.exasol.releaserobot;

import java.util.List;

import com.exasol.releaserobot.report.Report;

/**
 * Interface for the Release use case.
 */
public interface ReleaseUseCase {
    /**
     * Make a new release.
     *
     * @param userInput user input
     * @return list of reports
     */
    public List<Report> release(final UserInput userInput);
}
