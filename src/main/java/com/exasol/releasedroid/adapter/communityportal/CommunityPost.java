package com.exasol.releasedroid.adapter.communityportal;

import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

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
     * Get a teaser for an article. If the teaser was not set, it returns a random teaser.
     * 
     * @return teaser
     */
    public String getTeaser() {
        if (this.teaser == null || this.teaser.isEmpty()) {
            final Random random = new Random();
            final int index = random.nextInt(TEASERS.size());
            return TEASERS.get(index);
        } else {
            return this.teaser;
        }
    }

    /**
     * Transform the community post to the JSON format.
     * 
     * @return community post as a JSON string
     */
    public String toJson() {
        final JSONObject board = new JSONObject();
        board.put("id", getBoardId());
        final JSONObject contentWorkflowAction = new JSONObject();
        contentWorkflowAction.put("workflow_action", "save_draft");
        final JSONObject tags = new JSONObject();
        final JSONArray tagItems = new JSONArray();
        for (final String tag : getTags()) {
            tagItems.put(new JSONObject().put("text", tag));
        }
        tags.put("items", tagItems);
        final JSONObject data = new JSONObject();
        data.put("type", "message");
        data.put("board", board);
        data.put("subject", getHeader());
        data.put("body", getBody());
        data.put("teaser", getTeaser());
        data.put("tags", tags);
        data.put("content_workflow_action", contentWorkflowAction);
        final JSONObject body = new JSONObject();
        body.put("data", data);
        return body.toString();
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
        private String teaser;
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

        public Builder teaser(final String teaser) {
            this.teaser = teaser;
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