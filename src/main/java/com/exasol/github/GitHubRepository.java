package com.exasol.github;

import java.util.Optional;

/**
 * This interface represents a release-ready github repository.
 */
public interface GitHubRepository {
    /**
     * Get latest released tag if exists.
     *
     * @return release tag as a string or empty optional
     */
    public Optional<String> getLatestReleaseVersion();

    /**
     * Get a changelog file as a string.
     *
     * @return changelog file as a string
     */
    public String getChangelogFile();

    /**
     * Get a changes file as a string.
     *
     * @return changes file as a string
     */
    public String getChangesFile();

    /**
     * Release a new GitHub tag.
     *
     * @param tag tag as a String
     * @param name release name
     * @param releaseLetter release letter
     */
    public void release(String tag, String name, String releaseLetter);

    /**
     * Get a project version to be released.
     *
     * @return version as a string
     */
    public String getVersion();
}
