package com.exasol.releasedroid.usecases;

/**
 * Exception that happened during release process.
 */
public class ReleaseException extends Exception {
    private static final long serialVersionUID = 3528600229414385900L;

    /**
     * Create a new instance of {@link ReleaseException}.
     *
     * @param cause exception cause
     */
    public ReleaseException(final Throwable cause) {
        super(cause);
    }

    /**
     * Create a new instance of {@link ReleaseException}.
     *
     * @param message exception message
     */
    public ReleaseException(final String message) {
        super(message);
    }
}