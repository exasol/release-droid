package com.exasol.releaserobot;

import com.exasol.releaserobot.report.Report;

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
    public Report validate(UserInput userInput);
}