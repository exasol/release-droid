package com.exasol.github;

import java.io.IOException;
import java.util.logging.Logger;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.exasol.CredentialsProvider;
import com.exasol.git.GitRepository;

/**
 * Instantiates a {@link GitHubGitRepository}.
 */
public final class GitHubRepositoryFactory {
    private static final Logger LOGGER = Logger.getLogger(GitHubRepositoryFactory.class.getName());
    private static GitHubRepositoryFactory instance;

    private GitHubRepositoryFactory() {
        // prevent instantiation
    }

    /**
     * Get an instance of {@link GitHubRepositoryFactory}.
     *
     * @return instance of {@link GitHubRepositoryFactory}
     */
    public static synchronized GitHubRepositoryFactory getInstance() {
        if (instance == null) {
            instance = new GitHubRepositoryFactory();
        }
        return instance;
    }

    /**
     * Create a new {@link GitHubGitRepository}.
     * 
     * @param repositoryOwner repository owner on the GitHub
     * @param repositoryName repository name on the GitHub
     * @return new instance of {@link GitHubGitRepository}
     */
    public GitRepository createGitHubGitRepository(final String repositoryOwner, final String repositoryName) {
        final CredentialsProvider credentialsProvider = CredentialsProvider.getInstance();
        final GitHubUser gitHubUser = credentialsProvider.provideGitHubCredentials();
        final GHRepository repository = getLogInGitHubRepository(repositoryOwner, repositoryName,
                gitHubUser.getUsername(), gitHubUser.getToken());
        LOGGER.fine(() -> "Created an instance of GHRepository.");
        return new GitHubGitRepository(repository, gitHubUser);
    }

    private GHRepository getLogInGitHubRepository(final String repositoryOwner, final String repositoryName,
            final String username, final String oauthAccessToken) {
        try {
            final GitHub gitHub = GitHub.connect(username, oauthAccessToken);
            return gitHub.getRepository(repositoryOwner + "/" + repositoryName);
        } catch (final IOException exception) {
            final String message;
            if (exception.getMessage().contains("Not Found")) {
                message = "Repository '" + repositoryName
                        + "' not found. The repository doesn't exist or the user doesn't have permissions to see it.";
            } else if (exception.getMessage().contains("Bad credentials")) {
                message = "A GitHub account with specified username and password doesn't exist.";
            } else {
                message = exception.getMessage();
            }
            throw new GitHubException(message, exception);
        }
    }
}
