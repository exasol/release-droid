package com.exasol.releasedroid.usecases.validate;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.request.UserInput;

/**
 * Interface for the Validate use case.
 */
public interface ValidateUseCase {
    /**
     * Validate project.
     *
     * @param userInput user input
     * @return new {@link Report}
     */
    public Report validate(final UserInput userInput);
}