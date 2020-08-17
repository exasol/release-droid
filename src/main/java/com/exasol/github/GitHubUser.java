package com.exasol.github;

/**
 * Represents GitHub user.
 */
public class GitHubUser {
    private final String username;
    private final String token;

    /**
     * Create new {@link GitHubUser}.
     * 
     * @param username username as a string
     * @param token GitHub personal access token as a string
     */
    public GitHubUser(final String username, final String token) {
        this.username = username;
        this.token = token;
    }

    /**
     * Get a username.
     * 
     * @return username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Get a personal access token.
     * 
     * @return token
     */
    public String getToken() {
        return this.token;
    }
}