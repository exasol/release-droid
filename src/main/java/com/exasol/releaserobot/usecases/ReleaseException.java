package com.exasol.releaserobot.usecases;

/**
 * Exception that happened during release process.
 */
public class ReleaseException extends RuntimeException {
    private static final long serialVersionUID = 2855366863874311250L;

    /**
     * Create a new instance of {@link ReleaseException}.
     *
     * @param cause exception cause
     */
    public ReleaseException(final Throwable cause) {
        super(cause);
    }
}