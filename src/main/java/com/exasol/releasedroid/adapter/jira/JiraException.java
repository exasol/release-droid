package com.exasol.releasedroid.adapter.jira;

/**
 * This exceptions represents a problem while using Jira Gateway.
 */
public class JiraException extends Exception {
    private static final long serialVersionUID = -4825226450534300899L;

    /**
     * Create a new instance of {@link JiraException}.
     *
     * @param message message
     * @param cause   cause
     */
    public JiraException(final String message, final Throwable cause) {
        super(message, cause);
    }
}