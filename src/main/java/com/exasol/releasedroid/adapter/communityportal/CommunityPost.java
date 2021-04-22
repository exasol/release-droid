package com.exasol.releasedroid.adapter.communityportal;

import java.util.List;
import java.util.Objects;

/**
 * This class represents a post on the Exasol Community portal.
 */
public class CommunityPost {
    private final String header;
    private final String body;
    private final String boardId;
    private final String teaser;
    private final List<String> tags;

    private CommunityPost(final Builder builder) {
        this.header = builder.header;
        this.body = builder.body;
        this.boardId = builder.boardId;
        this.tags = builder.tags;
        this.teaser = builder.teaser;
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
     * Get the teaser.
     *
     * @return teaser
     */
    public String getTeaser() {
        return this.teaser;
    }

    /**
     * Create a new builder.
     * 
     * @return new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CommunityPost that = (CommunityPost) o;
        return this.header.equals(that.header) && this.body.equals(that.body) && this.boardId.equals(that.boardId)
                && this.tags.equals(that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.header, this.body, this.boardId, this.tags);
    }

    /**
     * Builder for {@link CommunityPost}.
     */
    public static class Builder {
        private String header;
        private String body;
        private String boardId;
        private String teaser;
        private List<String> tags;

        /**
         * Add a header.
         *
         * @param header header
         * @return builder
         */
        public Builder header(final String header) {
            this.header = header;
            return this;
        }

        /**
         * Add a body.
         *
         * @param body body
         * @return builder
         */
        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        /**
         * Add a board id.
         *
         * @param boardId the board id
         * @return the builder
         */
        public Builder boardId(final String boardId) {
            this.boardId = boardId;
            return this;
        }

        /**
         * Add a teaser.
         *
         * @param teaser teaser
         * @return builder
         */
        public Builder teaser(final String teaser) {
            this.teaser = teaser;
            return this;
        }

        /**
         * Add tags.
         *
         * @param tags tags
         * @return builder
         */
        public Builder tags(final List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Build a community post.
         *
         * @return community post
         */
        public CommunityPost build() {
            return new CommunityPost(this);
        }
    }
}