package com.exasol.releasedroid.adapter.communityportal;

import java.util.List;
import java.util.Random;

/**
 * This class represents a post on the Exasol Community portal.
 */
public class CommunityPost {
    private final static List<String> TEASERS = List.of("See what's new here.", "Learn more here.",
            "Find out what's new here.", "Find out how it can help you.",
            "Find out what's changed and where you can learn more.", "Read more here.", "Find out more here.",
            "Find out what this means, here.");
    private final String header;
    private final String body;
    private final String boardId;
    private final List<String> tags;

    private CommunityPost(final Builder builder) {
        this.header = builder.header;
        this.body = builder.body;
        this.boardId = builder.boardId;
        this.tags = builder.tags;
    }

    /**
     * Get the post's header.
     * 
     * @return post's header
     */
    public String getHeader() {
        return this.header;
    }

    /**
     * Get the post's body.
     * 
     * @return post's body
     */
    public String getBody() {
        return this.body;
    }

    /**
     * Get an id of the board to add the post to.
     * 
     * @return id of the board
     */
    public String getBoardId() {
        return this.boardId;
    }

    /**
     * Get a list of tags of the post.
     * 
     * @return list of tags of the post
     */
    public List<String> getTags() {
        return this.tags;
    }

    /**
     * Get a random teaser for an article.
     * 
     * @return random teaser
     */
    public String getTeaser() {
        final Random random = new Random();
        final int index = random.nextInt(TEASERS.size());
        return TEASERS.get(index);
    }

    /**
     * Create a new builder.
     * 
     * @return new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link CommunityPost}.
     */
    public static class Builder {
        private String header;
        private String body;
        private String boardId;
        private List<String> tags;

        public Builder header(final String header) {
            this.header = header;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public Builder boardId(final String boardId) {
            this.boardId = boardId;
            return this;
        }

        public Builder tags(final List<String> tags) {
            this.tags = tags;
            return this;
        }

        public CommunityPost build() {
            return new CommunityPost(this);
        }
    }
}