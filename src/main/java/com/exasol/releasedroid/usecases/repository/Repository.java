package com.exasol.releasedroid.usecases.repository;

import java.util.List;
import java.util.Map;

import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * This class represents a repository content based on the latest commit of the user-specified branch.
 */
public interface Repository extends RepositoryGate {
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

    public List<RepositoryValidator> getStructureValidators();

    Map<PlatformName, RepositoryValidator> getValidatorForPlatforms();
}