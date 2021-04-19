package com.exasol.releasedroid.adapter.communityportal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.jupiter.api.Test;

class CommunityPostTest {
    @Test
    void testToJson() {
        final CommunityPost communityPost = CommunityPost.builder() //
                .boardId("myBoard") //
                .teaser("My teaser") //
                .tags(List.of("Docker", "Exasol", "Integration")) //
                .header("New tech article!") //
                .body("Here is my article!") //
                .build();
        assertThat(communityPost.toJson(), equalTo("{\"data\":" //
                + "{\"subject\":\"New tech article!\"," //
                + "\"type\":\"message\"," //
                + "\"body\":\"Here is my article!\"," //
                + "\"board\":{\"id\":\"myBoard\"}," //
                + "\"content_workflow_action\":{\"workflow_action\":\"save_draft\"}," //
                + "\"teaser\":\"My teaser\"," //
                + "\"tags\":{\"items\":[{\"text\":\"Docker\"},{\"text\":\"Exasol\"},{\"text\":\"Integration\"}]}" //
                + "}" //
                + "}"));
    }
}