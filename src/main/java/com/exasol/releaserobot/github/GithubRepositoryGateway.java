package com.exasol.releaserobot.github;

import com.exasol.releaserobot.usecases.Repository;
import com.exasol.releaserobot.usecases.UserInput;
import com.exasol.releaserobot.usecases.validate.RepositoryGateway;

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
    public Repository getRepositoryWithBranch(final UserInput userInput) throws GitHubException {
        if (userInput.hasBranch()) {
            return this.githubGateway.getRepositoryWithUserSpecifiedBranch(userInput.getRepositoryName(),
                    userInput.getBranch());
        } else {
            return this.getRepositoryWithDefaultBranch(userInput.getRepositoryName());
        }
    }

    @Override
    public Repository getRepositoryWithDefaultBranch(final String repositoryFullName) throws GitHubException {
        return this.githubGateway.getRepositoryWithDefaultBranch(repositoryFullName);
    }
}