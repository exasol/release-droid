package com.exasol.release.robot.repository;

import org.kohsuke.github.GHRepository;

import com.exasol.release.robot.repository.maven.JavaMavenGitBranchContent;

/**
 * This class instantiates a {@link GitBranchContent} corresponding to the GitHub project's layout.
 */
final class GitHubRepositoryContentFactory {
    private static GitHubRepositoryContentFactory instance;

    private GitHubRepositoryContentFactory() {
        // prevent instantiation
    }

    /**
     * Get an instance of {@link GitHubRepositoryContentFactory}.
     * 
     * @return instance of {@link GitHubRepositoryContentFactory}
     */
    static synchronized GitHubRepositoryContentFactory getInstance() {
        if (instance == null) {
            instance = new GitHubRepositoryContentFactory();
        }
        return instance;
    }

    /**
     * Create a new instance of {@link GitBranchContent} from a GitHub repository depending on the repository's layout.
     * Currently always returns {@link JavaMavenGitBranchContent}.
     * 
     * @param repository instance of {@link GHRepository}
     * @param branchName name of a branch to read content from
     * @return new instance of {@link GitBranchContent}
     */
    GitBranchContent getGitHubRepositoryContent(final GHRepository repository, final String branchName) {
        return new JavaMavenGitBranchContent(repository, branchName);
    }
}