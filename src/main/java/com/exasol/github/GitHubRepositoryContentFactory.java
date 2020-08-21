package com.exasol.github;

import org.kohsuke.github.GHRepository;

import com.exasol.git.GitRepositoryContent;

/**
 * This class instantiates a {@link GitRepositoryContent} corresponding to the GitHub project's layout.
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
    public static synchronized GitHubRepositoryContentFactory getInstance() {
        if (instance == null) {
            instance = new GitHubRepositoryContentFactory();
        }
        return instance;
    }

    /**
     * Create a new instance of {@link GitRepositoryContent} from a GitHub repository depending on the repository's
     * layout. Currently always returns {@link JavaMavenGitRepositoryContent}.
     * 
     * @param repository instance of {@link GHRepository}
     * @param branchName name of a branch to read content from
     * @return new instance of {@link GitRepositoryContent}
     */
    GitRepositoryContent getGitHubRepositoryContent(final GHRepository repository, final String branchName) {
        return new JavaMavenGitRepositoryContent(repository, branchName);
    }
}