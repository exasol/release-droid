package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.github.GitHubException;
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
     * @throws GitHubException is some problem occurs
     */
    public Repository getRepository(UserInput userInput) throws GitHubException;

    /**
     * Get a default branch.
     *
     * @param repositoryFullName fully qualified name of the repository
     *
     * @return instance of {@link Repository}
     * @throws GitHubException is some problem occurs
     */
    public Repository getRepositoryWithDefaultBranch(String repositoryFullName) throws GitHubException;
}