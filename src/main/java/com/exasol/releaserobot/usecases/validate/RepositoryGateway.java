package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.usecases.Repository;
import com.exasol.releaserobot.usecases.UserInput;

/**
 * Gateway for interacting with repository.
 */
public interface RepositoryGateway {
    /**
     * Get a repository.
     *
     * @param userInput user input
     * @return instance of {@link Repository}
     */
    public Repository getRepositoryWithBranch(UserInput userInput);

    /**
     * Get a default branch.
     *
     * @param repositoryFullName fully qualified name of the repository
     *
     * @return instance of {@link Repository}
     */
    public Repository getRepositoryWithDefaultBranch(String repositoryFullName);
}