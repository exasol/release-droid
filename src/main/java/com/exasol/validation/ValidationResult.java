package com.exasol.validation;

/**
 * Represents a result of a single validation.
 */
public class ValidationResult {
    private final String errorCode;
    private final String message;
    private final boolean successful;

    private ValidationResult(final String errorCode, final String message, final boolean successful) {
        this.errorCode = errorCode;
        this.message = message;
        this.successful = successful;
    }

    /**
     * Check is a validation is successful.
     * 
     * @return true if a validation is successful
     */
    public boolean isSuccessful() {
        return this.successful;
    }

    /**
     * Create a new failed validation.
     * 
     * @param errorCode error code
     * @param message   error message
     * @return new instance of {@link ValidationResult}
     */
    public static ValidationResult failedValidation(final String errorCode, final String message) {
        return new ValidationResult(errorCode, message, false);
    }

    /**
     * Create a new successful validation.
     *
     * @param validatedContent description of the validated content
     * @return new instance of {@link ValidationResult}
     */
    public static ValidationResult successfulValidation(final String validatedContent) {
        return new ValidationResult(null, validatedContent, true);
    }

    @Override
    public String toString() {
        if (this.successful) {
            return "Success. " + this.message;
        } else {
            return "Fail.    " + this.errorCode + ": " + this.message;
        }
    }
}