package com.exasol.releasedroid.usecases;

import java.util.List;
import java.util.Map;

import com.exasol.releasedroid.repository.ReleaseLetter;
import com.exasol.releasedroid.repository.RepositoryGate;
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

    /**
     * Get key-value pairs for deliverable names and corresponding deliverable pathes.
     *
     * @return map with deliverables information
     */
    // [impl->dsn~repository-provides-deliverables-information~1]
    public Map<String, String> getDeliverables();

    /**
     * Get a primary programming language;
     *
     * @return programming language
     */
    public Language getRepositoryLanguage();

    public List<RepositoryValidator> getStructureValidators();

    enum Language {
        JAVA, SCALA, LANGUAGE_INDEPENDENT
    }

    Map<PlatformName, RepositoryValidator> getValidatorForPlatforms();
}