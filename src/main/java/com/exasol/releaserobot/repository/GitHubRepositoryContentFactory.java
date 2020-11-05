package com.exasol.releaserobot.repository;

import org.kohsuke.github.GHRepository;

import com.exasol.releaserobot.repository.maven.JavaMavenGitBranchContent;

/**
 * This class instantiates a {@link Branch} corresponding to the GitHub project's layout.
 */
public final class GitHubRepositoryContentFactory {
    private static GitHubRepositoryContentFactory instance;

    private GitHubRepositoryContentFactory() {
        // prevent instantiation
    }

    /**
     * Get an instance of {@link GitHubRepositoryContentFactory}.
     *
     * @return instance of {@link GitHubRepositoryContentFactory}
     */
    public static synchronized GitHubRepositoryContentFactory getInstance() {
        if (instance == null) {
            instance = new GitHubRepositoryContentFactory();
        }
        return instance;
    }

    /**
     * Create a new instance of {@link Branch} from a GitHub repository depending on the repository's layout. Currently
     * always returns {@link JavaMavenGitBranchContent}.
     *
     * @param repository instance of {@link GHRepository}
     * @param branchName name of a branch to read content from
     * @return new instance of {@link Branch}
     */
    public Branch getGitHubRepositoryContent(final GHRepository repository, final String branchName) {
        return new JavaMavenGitBranchContent(repository, branchName);
    }
}