package com.exasol.releaserobot.github;

/**
 * A GitHub project related exception.
 */
public class GitHubException extends RuntimeException {
    private static final long serialVersionUID = 8858322121654692542L;

    /**
     * Create a new instance of {@link GitHubException}.
     * 
     * @param message exception description
     * @param cause   exception cause
     */
    public GitHubException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new instance of {@link GitHubException}.
     *
     * @param message exception description
     */
    public GitHubException(final String message) {
        super(message);
    }
}