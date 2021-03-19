package com.exasol.releasedroid.usecases.release;

import java.util.List;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.request.UserInput;

/**
 * Interface for the Release use case.
 */
public interface ReleaseUseCase {
    /**
     * Make a new release.
     *
     * @param userInput user input
     * @return list of reports
     * @throws ReleaseException
     */
    public List<Report> release(final UserInput userInput) throws ReleaseException;
}