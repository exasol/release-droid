package com.exasol.releaserobot.usecases;

/**
 * Exception that happened during release process.
 */
public class ReleaseException extends Exception {
    private static final long serialVersionUID = 6230384073980892982L;

    /**
     * Create a new instance of {@link ReleaseException}.
     *
     * @param cause exception cause
     */
    public ReleaseException(final Throwable cause) {
        super(cause);
    }
}