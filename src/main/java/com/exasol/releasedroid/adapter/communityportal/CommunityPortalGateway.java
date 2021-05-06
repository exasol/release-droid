package com.exasol.releasedroid.adapter.communityportal;

/**
 * Gateway for interacting with Exasol Community Portal.
 */
public interface CommunityPortalGateway {
    /**
     * Create a draft post.
     * 
     * @param communityPost instance of {@link CommunityPost}
     * @return URL of the created draft
     */
    String sendDraftPost(CommunityPost communityPost) throws CommunityPortalException;
}