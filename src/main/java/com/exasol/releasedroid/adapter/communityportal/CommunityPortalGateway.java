package com.exasol.releasedroid.adapter.communityportal;

/**
 * Gateway for interacting with Exasol Community Portal.
 */
public interface CommunityPortalGateway {
    /**
     * Create a draft post.
     * 
     * @param communityPost instance of {@link CommunityPost}
     */
    void createDraftPost(CommunityPost communityPost) throws CommunityPortalException;
}