package com.exasol.releaserobot.report;

/**
 * A common interface for Release Robot actions results.
 */
public interface Result {
    /**
     * Check is an action was successful.
     * 
     * @return true if successful
     */
    public boolean isSuccessful();
}