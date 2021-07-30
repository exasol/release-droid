package com.exasol.releasedroid.adapter.github;

import java.io.IOException;

import org.kohsuke.github.GitHub;

import com.exasol.releasedroid.usecases.PropertyReader;

/**
 * GitHub Connector.
 */
public class GitHubConnectorImpl implements GitHubConnector {
    private GitHub gitHub;
    private final PropertyReader propertyReader;

    /**
     * Create a new instance of {@link GitHubConnectorImpl}.
     *
     * @param propertyReader property reader
     */
    public GitHubConnectorImpl(final PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    @Override
    public GitHub connectToGitHub() throws IOException {
        if (this.gitHub == null) {
            final String username = this.propertyReader.readProperty(GitHubConstants.GITHUB_USERNAME_KEY, false);
            final String token = this.propertyReader.readProperty(GitHubConstants.GITHUB_TOKEN_KEY, true);
            this.gitHub = GitHub.connect(username, token);
        }
        return this.gitHub;
    }
}