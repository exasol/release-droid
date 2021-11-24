package com.exasol.releasedroid.usecases.exception;

/**
 * Exception that happened during release process.
 */
public class ReleaseException extends RuntimeException {
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
     * @param message message
     * @param cause   exception cause
     */
    public ReleaseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new instance of {@link ReleaseException}.
     *
     * @param cause exception cause
     */
    public ReleaseException(final String message) {
        super(message);
    }
}