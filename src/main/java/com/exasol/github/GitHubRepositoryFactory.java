package com.exasol.github;

import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class instantiates a {@link GitHubRepository} corresponding to the project's main programming language.
 */
public final class GitHubRepositoryFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubRepositoryFactory.class);
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
     * Create a new {@link GitHubRepository}.
     * <p>
     * This method reads credentials from the {@code .release-robot/credentials} file in the user's home directory. If
     * the file does not exists or the credentials do not exist, it asks user to input credentials via terminal.
     * </p>
     *
     * @param repositoryOwner name of the owner on github
     * @param repositoryName name of the repository on github
     * @return currently always return an instance of {@link JavaMavenProject}
     */
    public GitHubRepository createGitHubRepository(final String repositoryOwner, final String repositoryName,
            final GitHubUser gitHubUser) {
        final String username = gitHubUser.getUsername();
        final String token = gitHubUser.getToken();
        final GHRepository ghRepository = getLogInGitHubRepository(repositoryOwner, repositoryName, username, token);
        return new JavaMavenProject(ghRepository, token);
    }

    private GHRepository getLogInGitHubRepository(final String repositoryOwner, final String repositoryName,
            final String username, final String oauthAccessToken) {
        try {
            final GitHub gitHub = getUserVerifiedGitHub(username, oauthAccessToken);
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

    private GitHub getUserVerifiedGitHub(final String username, final String oauthAccessToken) {
        LOGGER.debug("Creating a user-identified connection to the GitHub.");
        try {
            return GitHub.connect(username, oauthAccessToken);
        } catch (final IOException exception) {
            throw new GitHubException(
                    "Cannot create a user connection to the GitHub due to an error: " + exception.getMessage(),
                    exception);
        }
    }
}