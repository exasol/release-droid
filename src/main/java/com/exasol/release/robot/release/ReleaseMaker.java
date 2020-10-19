package com.exasol.release.robot.release;

/**
 * A common interface for classes performing releases on different release platforms.
 */
public interface ReleaseMaker {
    /**
     * Make a release.
     */
    boolean makeRelease();
}