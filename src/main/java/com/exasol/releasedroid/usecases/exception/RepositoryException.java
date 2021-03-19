package com.exasol.releasedroid.usecases.exception;

/**
 * A repository related exception.
 */
public class RepositoryException extends RuntimeException {
    private static final long serialVersionUID = 6632262488247260410L;

    public RepositoryException(final String message) {
        super(message);
    }

    public RepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RepositoryException(final Throwable cause) {
        super(cause);
    }
}