package com.exasol.releasedroid.usecases;

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
    // [impl->dsn~repository-retrieves-branch-content~1]
    public Repository getRepository(UserInput userInput);
}