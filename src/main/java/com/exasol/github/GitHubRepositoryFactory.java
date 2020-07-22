package com.exasol.github;

import java.io.*;
import java.util.*;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class instantiates a {@link GitHubRepository} corresponding to the project's main programming language.
 */
public class GitHubRepositoryFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubRepositoryFactory.class);
    private static final String USERNAME_KEY = "username";
    private static final String TOKEN_KEY = "token";
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
     * Create a new {@link GitHubRepository}. This method reads credentials from the '.release-robot/credentials' file
     * in the home directory. If the file does not exists or the credentials do not exist, it asks user to input
     * credentials via terminal.
     *
     * @param repositoryOwner name of the owner on github
     * @param repositoryName name of the repository on github
     * @return currently always return an instance of {@link JavaGitHubRepository}
     */
    public GitHubRepository createGitHubRepository(final String repositoryOwner, final String repositoryName) {
        final Map<String, String> credentials = getCredentials();
        final String username = credentials.get(USERNAME_KEY);
        final String token = credentials.get(TOKEN_KEY);
        final GHRepository ghRepository = getLogInGitHubRepository(repositoryOwner, repositoryName, username, token);
        return new JavaGitHubRepository(ghRepository, token);
    }

    private Map<String, String> getCredentials() {
        final Optional<Map<String, String>> properties = getCredentialsFromFile();
        if (properties.isPresent()) {
            LOGGER.info("Using credentials from file.");
            return properties.get();
        } else {
            LOGGER.info("Credentials are not found in the file.");
            return getCredentialsFromConsole();
        }
    }

    private Optional<Map<String, String>> getCredentialsFromFile() {
        LOGGER.info("Retrieving credentials from the file '.release-robot/credentials'.");
        final String homeDirectory = System.getProperty("user.home");
        final String credentialsPath = homeDirectory + "/.release-robot/credentials";
        try (final InputStream stream = new FileInputStream(credentialsPath)) {
            final Properties properties = new Properties();
            properties.load(stream);
            final Map<String, String> propertiesMap = new HashMap<>();
            final String username = properties.getProperty("github_username");
            final String token = properties.getProperty("github_oauth_access_token");
            if (username == null || token == null) {
                return Optional.empty();
            } else {
                propertiesMap.put(USERNAME_KEY, username);
                propertiesMap.put(TOKEN_KEY, token);
                return Optional.of(propertiesMap);
            }
        } catch (final IOException exception) {
            return Optional.empty();
        }
    }

    private Map<String, String> getCredentialsFromConsole() {
        final Console console = System.console();
        final String username = console.readLine("Enter username: ");
        final String token = new String(console.readPassword("Enter oauth access token: "));
        final Map<String, String> credentials = new HashMap<>();
        credentials.put(USERNAME_KEY, username);
        credentials.put(TOKEN_KEY, token);
        return credentials;
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
        LOGGER.info("Creating a user-identified connection to the GitHub.");
        try {
            return GitHub.connect(username, oauthAccessToken);
        } catch (final IOException exception) {
            throw new GitHubException(
                    "Cannot create a user connection to the GitHub due to an error: " + exception.getMessage(),
                    exception);
        }
    }
}