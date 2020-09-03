package com.exasol.repository;

import java.io.IOException;
import java.util.*;

import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;

import com.exasol.github.GitHubException;

/**
 * A GitHub-based repository.
 */
public class GitHubGitRepository implements GitRepository {
    private final GHRepository repository;
    private final Map<String, GitRepositoryContent> gitRepositoryContents = new HashMap<>();

    /**
     * Create a new instance of {@link GitHubGitRepository}.
     * 
     * @param repository instance of {@link GHRepository}
     */
    public GitHubGitRepository(final GHRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<String> getLatestTag() {
        try {
            final GHRelease release = this.repository.getLatestRelease();
            return (release == null) ? Optional.empty() : Optional.of(release.getTagName());
        } catch (final IOException exception) {
            throw new GitHubException("GitHub connection problem happened during retrieving the latest release.",
                    exception);
        }
    }

    @Override
    public String getDefaultBranchName() {
        return this.repository.getDefaultBranch();
    }

    @Override
    public synchronized GitRepositoryContent getRepositoryContent(final String branchName) {
        if (!this.gitRepositoryContents.containsKey(branchName)) {
            final GitRepositoryContent gitHubRepositoryContent = GitHubRepositoryContentFactory.getInstance()
                    .getGitHubRepositoryContent(this.repository, branchName);
            this.gitRepositoryContents.put(branchName, gitHubRepositoryContent);
        }
        return this.gitRepositoryContents.get(branchName);
    }
}