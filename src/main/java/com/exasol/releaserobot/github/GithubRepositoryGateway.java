package com.exasol.releaserobot.github;

import java.util.Optional;

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
    public Repository getRepository(final UserInput userInput) throws GitHubException {
        final Repository branch = this.getBranch(userInput);
        final Optional<String> latestTag = this.githubGateway.getLatestTag(userInput.getRepositoryFullName());
        return new Repository(latestTag, branch);
    }

    private Repository getBranch(final UserInput userInput) throws GitHubException {
        if (userInput.hasGitBranch()) {
            return this.githubGateway.getBranch(userInput.getRepositoryFullName(), userInput.getGitBranch());
        }
        return this.getDefaultBranch(userInput.getRepositoryFullName());
    }

    @Override
    public Repository getDefaultBranch(final String repositoryFullName) throws GitHubException {
        return this.githubGateway.getDefaultBranch(repositoryFullName);
    }
}
