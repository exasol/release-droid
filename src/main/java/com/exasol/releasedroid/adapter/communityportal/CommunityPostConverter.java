package com.exasol.releasedroid.adapter.communityportal;

import java.util.List;

import jakarta.json.*;

/**
 * Converter for Community Post.
 */
public class CommunityPostConverter {
    private CommunityPostConverter() {
    }

    /**
     * Transform the community post to the JSON format.
     *
     * @param communityPost post to be converted to Json
     * @return community post as a JSON string
     */
    public static String toJson(final CommunityPost communityPost) {
        final JsonArrayBuilder tagItems = Json.createArrayBuilder();
        for (final String tag : communityPost.getTags()) {
            tagItems.add(Json.createObjectBuilder().add("text", tag));
        }

        final JsonObject jsonBody = Json.createObjectBuilder() //
                .add("data", Json.createObjectBuilder() //
                        .add("subject", communityPost.getHeader()) //
                        .add("type", "message") //
                        .add("body", communityPost.getBody()) //
                        .add("board", Json.createObjectBuilder() //
                                .add("id", communityPost.getBoardId())) //
                        .add("content_workflow_action", Json.createObjectBuilder() //
                                .add("workflow_action", "submit_for_review")) //
                        .add("teaser", getTeaser(communityPost)) //
                        .add("tags", Json.createObjectBuilder() //
                                .add("items", tagItems))) //
                .build();

        return jsonBody.toString();
    }

    private static String getTeaser(final CommunityPost communityPost) {
        final String teaser = communityPost.getTeaser();
        if ((teaser == null) || teaser.isEmpty()) {
            final List<String> teasers = List.of("See what's new here.", "Learn more here.",
                    "Find out what's new here.", "Find out how it can help you.",
                    "Find out what's changed and where you can learn more.", "Read more here.", "Find out more here.",
                    "Find out what this means, here.");
            return teasers.get(communityPost.getBody().length() % teasers.size());
        } else {
            return teaser;
        }
    }
}