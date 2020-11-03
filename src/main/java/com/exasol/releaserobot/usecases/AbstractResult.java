package com.exasol.releaserobot.usecases;

/**
 * An abstract base for Release Robot actions results.
 */
public abstract class AbstractResult implements Result {
    private final boolean successful;

    protected AbstractResult(final boolean successful) {
        this.successful = successful;
    }

    /**
     * Check is a validation is successful.
     *
     * @return true if a validation is successful
     */
    @Override
    public boolean isSuccessful() {
        return this.successful;
    }
}