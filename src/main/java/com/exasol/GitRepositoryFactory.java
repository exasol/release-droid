package com.exasol;

import java.io.IOException;
import java.util.logging.Logger;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.exasol.github.*;

/**
 * Instantiates different {@link GitRepository}s.
 */
public class GitRepositoryFactory {
    private static final Logger LOGGER = Logger.getLogger(GitRepositoryFactory.class.getName());
    private static GitRepositoryFactory instance;

    private GitRepositoryFactory() {
        // prevent instantiation
    }

    /**
     * Get an instance of {@link GitRepositoryFactory}.
     *
     * @return instance of {@link GitRepositoryFactory}
     */
    public static synchronized GitRepositoryFactory getInstance() {
        if (instance == null) {
            instance = new GitRepositoryFactory();
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
