package com.exasol.platform;

import java.io.Console;
import java.io.IOException;

import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubInstance {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubInstance.class);

    private GitHubInstance() {
        // prevent instantiation
    }

    public static GitHub getAnonymousGitHub() {
        LOGGER.info("Creating an anonymous connection to GitHub.");
        try {
            return GitHub.connectAnonymously();
        } catch (final IOException exception) {
            throw new IllegalStateException("Cannot connect to the GitHub due to an error: " + exception.getMessage());
        }
    }

    public static GitHub getUserVerifiedGitHub() {
        LOGGER.info("Creating a user-identified connection to GitHub.");
        final Console console = System.console();
        final String username = console.readLine("Username: ");
        final String oauthAccessToken = new String(console.readPassword("Oauth Access Token: "));
        try {
            return GitHub.connect(username, oauthAccessToken);
        } catch (final IOException exception) {
            throw new IllegalStateException("Cannot connect to the GitHub due to an error: " + exception.getMessage());
        }
    }
}