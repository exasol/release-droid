package com.exasol.releaserobot.github;

import java.util.Optional;

import com.exasol.releaserobot.repository.*;
import com.exasol.releaserobot.usecases.UserInput;
import com.exasol.releaserobot.usecases.validate.RepositoryGateway;

public class GithubRepositoryGateway implements RepositoryGateway {
    private final GithubGateway githubGateway;

    public GithubRepositoryGateway(final GithubGateway githubGateway) {
        super();
        this.githubGateway = githubGateway;
    }

    @Override
    public Repository getRepository(final UserInput userInput) {
        final Branch branch = this.getBranch(userInput);
        final Optional<String> latestTag = this.githubGateway.getLatestTag();
        return new GitHubGitRepository(latestTag, branch);
    }

    private Branch getBranch(final UserInput userInput) {
        if (userInput.hasGitBranch()) {
            return this.githubGateway.getBranch(userInput.getGitBranch());
        }
        return this.getDefaultBranch();
    }

    @Override
    public Branch getDefaultBranch() {
        return this.githubGateway.getDefaultBranch();
    }

}
