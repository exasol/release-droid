package com.exasol.releasedroid.usecases.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * This class represents a repository content based on the latest commit of the user-specified branch.
 */
public interface Repository extends RepositoryGate {
    /**
     * Get a release config.
     *
     * @return release config
     */
    public Optional<ReleaseConfig> getReleaseConfig();

    /**
     * Get a changelog file as a string.
     *
     * @return changelog file as a string
     */
    public String getChangelogFile();

    /**
     * Get a changes file as an instance of {@link ReleaseLetter}.
     *
     * @param version version as a string
     * @return release changes file
     */
    public ReleaseLetter getReleaseLetter(final String version);

    /**
     * Get a current project version.
     *
     * @return version as a string
     */
    // [impl->dsn~repository-provides-current-version~1]
    public String getVersion();

    /**
     * Get the repository validators.
     *
     * @return repository validators
     */
    public List<RepositoryValidator> getRepositoryValidators();

    /**
     * Get the platform validators.
     *
     * @return platform validators
     */
    public Map<PlatformName, ReleasePlatformValidator> getPlatformValidators();
}