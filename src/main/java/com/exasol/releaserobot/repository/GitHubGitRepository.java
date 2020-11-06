package com.exasol.releaserobot.repository;

import java.util.Optional;

/**
 * A GitHub-based repository.
 */
public class GitHubGitRepository implements Repository {
    private final Optional<String> latestTag;
    private final Branch branch;

    /**
     * Create a new instance of {@link GitHubGitRepository}.
     *
     * @param latestTag latest tag
     * @param branch    instance of {@link Branch}
     */
    public GitHubGitRepository(final Optional<String> latestTag, final Branch branch) {
        this.latestTag = latestTag;
        this.branch = branch;
    }

    @Override
    public Optional<String> getLatestTag() {
        return this.latestTag;
    }

    @Override
    public Branch getBranch() {
        return this.branch;
    }
}