package com.exasol.releasedroid.repository;

import com.exasol.releasedroid.github.GitHubException;
import com.exasol.releasedroid.github.GithubGateway;
import com.exasol.releasedroid.usecases.*;

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
    public Repository getRepository(final UserInput userInput) {
        final String branch = getBranch(userInput);
        return getRepository(userInput.getRepositoryName(), branch);
    }

    private String getBranch(final UserInput userInput) {
        if (userInput.hasBranch()) {
            return userInput.getBranch();
        } else {
            try {
                return this.githubGateway.getDefaultBranch(userInput.getRepositoryName());
            } catch (final GitHubException exception) {
                throw new RepositoryException(exception);
            }
        }
    }

    private Repository getRepository(final String repositoryFullName, final String branchName) {
        try {
            return new GitHubRepository(this.githubGateway, branchName, repositoryFullName,
                    this.githubGateway.getLatestTag(repositoryFullName));
        } catch (final GitHubException exception) {
            throw new RepositoryException(exception);
        }
    }
}