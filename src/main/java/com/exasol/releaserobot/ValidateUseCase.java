package com.exasol.releaserobot;

import com.exasol.releaserobot.report.ValidationReport;

/**
 * Contains validation-related methods.
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