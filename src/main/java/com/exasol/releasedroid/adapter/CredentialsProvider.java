package com.exasol.releasedroid.adapter;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class provides user credentials for different platforms.
 */
public final class CredentialsProvider {
    private static final Logger LOGGER = Logger.getLogger(CredentialsProvider.class.getName());
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String RELEASE_DROID_CREDENTIALS = FILE_SEPARATOR + ".release-droid" + FILE_SEPARATOR
            + "credentials";
    private static final String GITHUB_USERNAME_KEY = "github_username";
    private static final String GITHUB_TOKEN_KEY = "github_oauth_access_token";
    private static final String COMMUNITY_USERNAME_KEY = "community_username";
    private static final String COMMUNITY_PASSWORD_KEY = "community_password";
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
     * @return new instance of {@link User}
     */
    public User provideGitHubUser() {
        return createUserWithUserNameAndPassword(GITHUB_USERNAME_KEY, GITHUB_TOKEN_KEY);
    }

    private User createUserWithUserNameAndPassword(final String usernameKey, final String usernamePassword) {
        final Map<String, String> credentials = getCredentials(usernameKey, usernamePassword);
        final String username = credentials.get(usernameKey);
        final String token = credentials.get(usernamePassword);
        return new User(username, token);
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
        LOGGER.fine(() -> "Retrieving credentials from the file '" + RELEASE_DROID_CREDENTIALS + "'.");
        final String homeDirectory = System.getProperty("user.home");
        final String credentialsPath = homeDirectory + RELEASE_DROID_CREDENTIALS;
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

    /**
     * Get Exasol Community Portal credentials.
     *
     * @return new instance of {@link User}
     */
    public User provideCommunityPortalUser() {
        return createUserWithUserNameAndPassword(COMMUNITY_USERNAME_KEY, COMMUNITY_PASSWORD_KEY);
    }
}