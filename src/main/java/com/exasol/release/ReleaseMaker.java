package com.exasol.release;

/**
 * A common interface for classes performing releases.
 */
public interface ReleaseMaker {
    /**
     * Make a release.
     */
    void makeRelease();
}