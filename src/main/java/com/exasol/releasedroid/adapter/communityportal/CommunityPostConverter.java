package com.exasol.releasedroid.adapter.communityportal;

import java.security.SecureRandom;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Converter for Community Post.
 */
public class CommunityPostConverter {
    private CommunityPostConverter() {
    }

    /**
     * Transform the community post to the JSON format.
     *
     * @return community post as a JSON string
     */
    public static String toJson(final CommunityPost communityPost) {
        final var board = new JSONObject();
        board.put("id", communityPost.getBoardId());
        final var contentWorkflowAction = new JSONObject();
        contentWorkflowAction.put("workflow_action", "save_draft");
        final var jsonTags = new JSONObject();
        final var tagItems = new JSONArray();
        for (final var tag : communityPost.getTags()) {
            tagItems.put(new JSONObject().put("text", tag));
        }
        jsonTags.put("items", tagItems);
        final var data = new JSONObject();
        data.put("type", "message");
        data.put("board", board);
        data.put("subject", communityPost.getHeader());
        data.put("body", communityPost.getBody());
        data.put("teaser", getTeaser(communityPost));
        data.put("tags", jsonTags);
        data.put("content_workflow_action", contentWorkflowAction);
        final var jsonBody = new JSONObject();
        jsonBody.put("data", data);
        return jsonBody.toString();
    }

    private static String getTeaser(final CommunityPost communityPost) {
        final String teaser = communityPost.getTeaser();
        if (teaser == null || teaser.isEmpty()) {
            final List<String> TEASERS = List.of("See what's new here.", "Learn more here.",
                    "Find out what's new here.", "Find out how it can help you.",
                    "Find out what's changed and where you can learn more.", "Read more here.", "Find out more here.",
                    "Find out what this means, here.");
            final SecureRandom random = new SecureRandom();
            return TEASERS.get(random.nextInt(TEASERS.size()));
        } else {
            return teaser;
        }
    }
}