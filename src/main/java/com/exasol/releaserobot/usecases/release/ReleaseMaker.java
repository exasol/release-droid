package com.exasol.releaserobot.usecases.release;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.repository.Branch;

/**
 * A common interface for classes performing releases on different release platforms.
 */
public interface ReleaseMaker {
    /**
     * Make a release.
     *
     * @throws GitHubException if release fails
     */
    void makeRelease(final Branch branch) throws GitHubException;
}