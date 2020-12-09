package com.exasol.releasedroid.usecases.report;

/**
 * An abstract base for Release Droid actions results.
 */
public abstract class Result {
    private final boolean successful;

    protected Result(final boolean successful) {
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
}