package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.repository.Branch;
import com.exasol.releaserobot.repository.Repository;
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
    public Repository getRepository(UserInput userInput);

    /**
     * Get a default branch.
     * 
     * @return instance of {@link Branch}
     */
    public Branch getDefaultBranch();
}