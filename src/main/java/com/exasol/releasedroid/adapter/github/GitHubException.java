package com.exasol.releasedroid.adapter.github;

/**
 * A GitHub related exception.
 */
public class GitHubException extends Exception {
    private static final long serialVersionUID = -4376831034407541999L;

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