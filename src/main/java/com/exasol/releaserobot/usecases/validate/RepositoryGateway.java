package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.repository.RepositoryTOGOAWAY;
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
     * @return instance of {@link RepositoryTOGOAWAY}
     * @throws GitHubException
     */
    public Repository getRepository(UserInput userInput) throws GitHubException;

    /**
     * Get a default branch.
     *
     * @param repositoryFullName
     *
     * @return instance of {@link Repository}
     * @throws GitHubException
     */
    public Repository getDefaultBranch(String repositoryFullName) throws GitHubException;
}