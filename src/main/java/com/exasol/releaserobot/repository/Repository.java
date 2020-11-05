package com.exasol.releaserobot.repository;

import java.util.Optional;

/**
 * Represents a repository where a Git-based project is stored. A repository can be local or remote (GitHub, for
 * example).
 */
public interface Repository {
    /**
     * Get the latest tag if exists.
     *
     * @return latest tag as a string or empty optional
     */
    public Optional<String> getLatestTag();

    /**
     * Get a branch.
     *
     * @return instance of {@link Branch}
     */
    // [impl->dsn~gr-retrieves-branch-content~1]
    public Branch getBranch();
}