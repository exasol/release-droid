package com.exasol.releaserobot;

import com.exasol.releaserobot.report.ValidationReport;

/**
 * Interface for the Validate use case.
 */
public interface ValidateUseCase {
    /**
     * Validate project.
     * 
     * @param userInput user input
     * @return new {@link ValidationReport}
     */
    public ValidationReport validate(UserInput userInput);
}