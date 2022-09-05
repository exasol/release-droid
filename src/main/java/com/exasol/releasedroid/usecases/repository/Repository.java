package com.exasol.releasedroid.usecases.repository;

import java.util.*;

import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * This class represents a repository content based on the latest commit of the user-specified branch.
 */
public interface Repository extends RepositoryGate {
    /**
     * @return release configuration
     */
    Optional<ReleaseConfig> getReleaseConfig();

    /**
     * @return changelog file as a string
     */
    String getChangelog();

    /**
     * Get a changes file as an instance of {@link ReleaseLetter}.
     *
     * @param version version as a string
     * @return release changes file
     */
    ReleaseLetter getReleaseLetter(final String version);

    /**
     * @return current project version as a string
     */
    // [impl->dsn~repository-provides-current-version~1]
    String getVersion();

    /**
     * @return git tags of the repository
     */
    // [impl->dsn~creating-git-tags~1]
    List<String> getGitTags();

    /**
     * @return repository validators
     */
    List<RepositoryValidator> getRepositoryValidators();

    /**
     * @return platform validators
     */
    Map<PlatformName, ReleasePlatformValidator> getPlatformValidators();
}
