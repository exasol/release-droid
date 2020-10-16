package com.exasol.report;

/**
 * Represents a validation report.
 */
// [impl->dsn~rr-creates-validation-report~1]
public class ValidationReport extends AbstractReport {
    /**
     * Register a new successful validation.
     * 
     * @param validatedContent description of the validated content
     */
    public synchronized void addSuccessfulValidation(final String validatedContent) {
        this.results.add(ValidationResult.successfulValidation(validatedContent));
    }

    /**
     * Register a new failed validation.
     * 
     * @param errorCode error code
     * @param message   error message
     */
    public synchronized void addFailedValidations(final String errorCode, final String message) {
        this.results.add(ValidationResult.failedValidation(errorCode, message));
        super.hasFailures = true;
    }

    @Override
    public String getShortDescription() {
        return getShortDescription("Validation");
    }
}