package com.exasol.github;

import java.io.Console;
import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class instantiates a {@link GitHubRepository} corresponding to the project's main programming language.
 */
public class GitHubRepositoryFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubRepositoryFactory.class);

    private GitHubRepositoryFactory() {
        // prevent instantiation
    }

    /**
     * Try to create a new {@link GitHubRepository} without user credentials. If the repository is not found in the list
     * of public repositories, switches to user identified account.
     *
     * @param repositoryOwner name of the owner on github
     * @param repositoryName name of the repository on github
     * @return currently always return an instance of {@link JavaGitHubRepository}
     */
    public static AbstractGitHubRepository getAnonymousGitHubRepository(final String repositoryOwner,
            final String repositoryName) {
        final GitHub github = getAnonymousGitHub();
        try {
            final GHRepository ghRepository = github.getRepository(repositoryOwner + "/" + repositoryName);
            return new JavaGitHubRepository(ghRepository);
        } catch (final IOException exception) {
            LOGGER.info(
                    "Repository '{}' not found in the list of public repositories of the owner '{}'. "
                            + "Log in as a GitHub user to search in the private repositories too.",
                    repositoryName, repositoryOwner);
            return getLogInGitHubRepository(repositoryOwner, repositoryName);
        }
    }

    /**
     * Create a new {@link GitHubRepository} with a logged-in user.
     *
     * @param repositoryOwner name of the owner on github
     * @param repositoryName name of the repository on github
     * @return currently always return an instance of {@link JavaGitHubRepository}
     */
    public static AbstractGitHubRepository getLogInGitHubRepository(final String repositoryOwner,
            final String repositoryName) {
        final GitHub github = getUserVerifiedGitHub();
        try {
            final GHRepository ghRepository = github.getRepository(repositoryOwner + "/" + repositoryName);
            return new JavaGitHubRepository(ghRepository);
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

    private static GitHub getAnonymousGitHub() {
        LOGGER.info("Creating an anonymous connection to the GitHub.");
        try {
            return GitHub.connectAnonymously();
        } catch (final IOException exception) {
            throw new GitHubException(
                    "Cannot create an anonymous connection to the GitHub due to an error: " + exception.getMessage(),
                    exception);
        }
    }

    private static GitHub getUserVerifiedGitHub() {
        LOGGER.info("Creating a user-identified connection to the GitHub.");
        final Console console = System.console();
        final String username = console.readLine("Enter username: ");
        final String oauthAccessToken = new String(console.readPassword("Enter oauth access token: "));
        try {
            return GitHub.connect(username, oauthAccessToken);
        } catch (final IOException exception) {
            throw new GitHubException(
                    "Cannot create a user connection to the GitHub due to an error: " + exception.getMessage(),
                    exception);
        }
    }
}