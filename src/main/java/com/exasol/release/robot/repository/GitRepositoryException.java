package com.exasol.release.robot.repository;

/**
 * A git repository related exception.
 */
public class GitRepositoryException extends RuntimeException {
    private static final long serialVersionUID = 6632262488247260410L;

    public GitRepositoryException(final String message) {
        super(message);
    }

    public GitRepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
