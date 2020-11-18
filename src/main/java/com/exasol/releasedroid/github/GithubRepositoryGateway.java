package com.exasol.releasedroid.github;

import com.exasol.releasedroid.repository.RepositoryException;
import com.exasol.releasedroid.usecases.Repository;
import com.exasol.releasedroid.usecases.UserInput;
import com.exasol.releasedroid.usecases.validate.RepositoryGateway;

/**
 * Implements a GitHub-based repository gateway.
 */
public class GithubRepositoryGateway implements RepositoryGateway {
    private final GithubGateway githubGateway;

    /**
     * Create a new instance of {@link GithubGateway}.
     *
     * @param githubGateway instance of {@link GithubGateway}
     */
    public GithubRepositoryGateway(final GithubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    @Override
    public Repository getRepositoryWithBranch(final UserInput userInput) {
        if (userInput.hasBranch()) {
            try {
                return this.githubGateway.getRepositoryWithUserSpecifiedBranch(userInput.getRepositoryName(),
                        userInput.getBranch());
            } catch (final GitHubException exception) {
                throw new RepositoryException(exception);
            }
        } else {
            return this.getRepositoryWithDefaultBranch(userInput.getRepositoryName());
        }
    }

    @Override
    public Repository getRepositoryWithDefaultBranch(final String repositoryFullName) {
        try {
            return this.githubGateway.getRepositoryWithDefaultBranch(repositoryFullName);
        } catch (final GitHubException exception) {
            throw new RepositoryException(exception);
        }
    }
}