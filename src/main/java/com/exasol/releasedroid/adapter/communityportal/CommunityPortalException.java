package com.exasol.releasedroid.adapter.communityportal;

/**
 * This exceptions represents a problem while using Community Portal Gateway.
 */
public class CommunityPortalException extends Exception {

    private static final long serialVersionUID = -2951617373320626281L;

    /**
     * New instance of {@link CommunityPortalException}
     *
     * @param message message to be used
     */
    public CommunityPortalException(final String message) {
        super(message);
    }

    /**
     * New instance of {@link CommunityPortalException}
     *
     * @param cause original cause of the exception
     */
    public CommunityPortalException(final Throwable cause) {
        super(cause);
    }
}