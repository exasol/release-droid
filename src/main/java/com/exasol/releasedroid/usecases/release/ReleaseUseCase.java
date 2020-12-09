package com.exasol.releasedroid.usecases.release;

import java.util.List;

import com.exasol.releasedroid.usecases.UserInput;
import com.exasol.releasedroid.usecases.report.Report;

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