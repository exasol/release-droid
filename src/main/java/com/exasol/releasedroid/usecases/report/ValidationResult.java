package com.exasol.releasedroid.usecases.report;

/**
 * Represents a result of a single validation.
 */
public class ValidationResult extends Result {
    private final String message;

    private ValidationResult(final String message, final boolean successful) {
        super(successful);
        this.message = message;
    }

    /**
     * Create a new failed validation.
     *
     * @param message error message
     * @return new instance of {@link ValidationResult}
     */
    public static ValidationResult failedValidation(final String message) {
        return new ValidationResult(message, false);
    }

    /**
     * Create a new successful validation.
     *
     * @param validatedContent description of the validated content
     * @return new instance of {@link ValidationResult}
     */
    public static ValidationResult successfulValidation(final String validatedContent) {
        return new ValidationResult(validatedContent, true);
    }

    @Override
    public String toString() {
        if (isSuccessful()) {
            return "Success. " + this.message;
        } else {
            return "Fail.    " + this.message;
        }
    }
}