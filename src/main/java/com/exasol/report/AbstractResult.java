package com.exasol.report;

/**
 * An abstract base for Release Robot actions results.
 */
public abstract class AbstractResult implements Result {
    private final boolean successful;

    protected AbstractResult(boolean successful) {
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