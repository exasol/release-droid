package com.exasol.releasedroid.adapter.communityportal;

/**
 * This exceptions represents a problem while using Community Portal Gateway.
 */
public class CommunityPortalException extends Exception {
    private static final long serialVersionUID = -2951617373320626281L;

    public CommunityPortalException(final String message) {
        super(message);
    }

    public CommunityPortalException(final Throwable cause) {
        super(cause);
    }
}