package com.exasol.releaserobot.usecases;

/**
 * Represents a result of a single validation.
 */
public class ValidationResult extends AbstractResult {
    private final String errorCode;
    private final String message;

    private ValidationResult(final String errorCode, final String message, final boolean successful) {
        super(successful);
        this.errorCode = errorCode;
        this.message = message;
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
        if (isSuccessful()) {
            return "Success. " + this.message;
        } else {
            return "Fail.    " + this.errorCode + ": " + this.message;
        }
    }
}