package com.exasol.release.robot;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.release.robot.github.GitHubUser;

/**
 * This class provides user credentials for different platforms.
 */
public final class CredentialsProvider {
    private static final Logger LOGGER = Logger.getLogger(CredentialsProvider.class.getName());
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String RELEASE_ROBOT_CREDENTIALS = FILE_SEPARATOR + ".release-robot" + FILE_SEPARATOR
            + "credentials";
    private static final String GITHUB_USERNAME_KEY = "github_username";
    private static final String GITHUB_TOKEN_KEY = "github_oauth_access_token";
    private static CredentialsProvider credentialsProvider;

    private CredentialsProvider() {
        // prevents instantiation
    }

    /**
     * Get an instance of {@link CredentialsProvider}.
     *
     * @return instance of {@link CredentialsProvider}
     */
    public static CredentialsProvider getInstance() {
        if (credentialsProvider == null) {
            credentialsProvider = new CredentialsProvider();
        }
        return credentialsProvider;
    }

    /**
     * Get GitHub credentials.
     *
     * @return new instance of {@link GitHubUser}
     */
    public GitHubUser provideGitHubUserWithCredentials() {
        final Map<String, String> credentials = getCredentials(GITHUB_USERNAME_KEY, GITHUB_TOKEN_KEY);
        final String username = credentials.get(GITHUB_USERNAME_KEY);
        final String token = credentials.get(GITHUB_TOKEN_KEY);
        return new GitHubUser(username, token);
    }

    private Map<String, String> getCredentials(final String... mapKeys) {
        final Optional<Map<String, String>> properties = getCredentialsFromFile(mapKeys);
        if (properties.isPresent()) {
            LOGGER.fine(() -> "Using credentials from file.");
            return properties.get();
        } else {
            LOGGER.fine(() -> "Credentials are not found in the file.");
            return getCredentialsFromConsole(mapKeys);
        }
    }

    private Optional<Map<String, String>> getCredentialsFromFile(final String... mapKeys) {
        LOGGER.fine(() -> "Retrieving credentials from the file '" + RELEASE_ROBOT_CREDENTIALS + "'.");
        final String homeDirectory = System.getProperty("user.home");
        final String credentialsPath = homeDirectory + RELEASE_ROBOT_CREDENTIALS;
        return readCredentialsFromFile(credentialsPath, mapKeys);
    }

    private Optional<Map<String, String>> readCredentialsFromFile(final String credentialsPath,
            final String... mapKeys) {
        try (final InputStream stream = new FileInputStream(credentialsPath)) {
            final Properties properties = new Properties();
            properties.load(stream);
            final Map<String, String> propertiesMap = new HashMap<>();
            for (final String key : mapKeys) {
                final String value = properties.getProperty(key);
                if (value == null) {
                    return Optional.empty();
                } else {
                    propertiesMap.put(key, value);
                }
            }
            return Optional.of(propertiesMap);
        } catch (final IOException exception) {
            return Optional.empty();
        }
    }

    private Map<String, String> getCredentialsFromConsole(final String... mapKeys) {
        final Console console = System.console();
        final Map<String, String> credentials = new HashMap<>();
        for (final String key : mapKeys) {
            final String value = console.readLine("Enter " + key.replace("_", " "));
            credentials.put(key, value);
        }
        return credentials;
    }
}