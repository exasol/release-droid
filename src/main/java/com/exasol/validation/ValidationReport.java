package com.exasol.validation;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a validation report.
 */
// [impl->dsn~rr-creates-validation-report~1]
public class ValidationReport {
    private final List<ValidationResult> validationResults = new LinkedList<>();
    private boolean hasFailedValidations = false;

    /**
     * Register a new successful validation.
     * 
     * @param validatedContent description of the validated content
     */
    public synchronized void addSuccessfulValidation(final String validatedContent) {
        this.validationResults.add(ValidationResult.successfulValidation(validatedContent));
    }

    /**
     * Register a new failed validation.
     * 
     * @param errorCode error code
     * @param message   error message
     */
    public synchronized void addFailedValidations(final String errorCode, final String message) {
        this.validationResults.add(ValidationResult.failedValidation(errorCode, message));
        this.hasFailedValidations = true;
    }

    /**
     * Check if validation report has failed validations.
     * 
     * @return true if one or more failed validation presents
     */
    public boolean hasFailedValidations() {
        return this.hasFailedValidations;
    }

    /**
     * Get a failed validations report.
     * 
     * @return report as a string
     */
    public String getFailedValidations() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for (final ValidationResult validationResult : this.validationResults) {
            if (!validationResult.isSuccessful()) {
                stringBuilder.append(validationResult.toString());
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Get a report with all validations' results.
     * 
     * @return report as a string
     */
    public String getFullReport() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final ValidationResult validationResult : this.validationResults) {
            stringBuilder.append(validationResult.toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}