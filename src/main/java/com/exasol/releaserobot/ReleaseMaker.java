package com.exasol.releaserobot;

import com.exasol.releaserobot.github.GitHubException;

/**
 * A common interface for classes performing releases on different release platforms.
 */
public interface ReleaseMaker {
    /**
     * Make a release.
     * 
     * @throws GitHubException if release fails
     */
    public void makeRelease() throws GitHubException;
}