package com.exasol.releasedroid.usecases.repository;

import com.exasol.releasedroid.usecases.request.UserInput;

/**
 * Gateway for interacting with repository.
 */
public interface RepositoryGateway {
    /**
     * Get the repository specified in the given UserInput from the repository source. 
     *
     * @param userInput user input
     * @return instance of {@link Repository}
     */
    // [impl->dsn~repository-retrieves-branch-content~1]
    public Repository getRepository(UserInput userInput);
}