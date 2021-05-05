package com.exasol.releasedroid.adapter;

/**
 * Represents a user with username and password.
 */
public class User {
    private final String username;
    private final String password;

    /**
     * Create new {@link User}.
     *
     * @param username username as a string
     * @param password password as a string
     */
    public User(final String username, final String password) {
        this.username = username;
        this.password = password;
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
     * Get a password.
     *
     * @return password
     */
    public String getPassword() {
        return this.password;
    }
}