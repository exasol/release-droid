package com.exasol.releasedroid.adapter.communityportal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.jupiter.api.Test;

class CommunityPostConverterTest {
    @Test
    void testToJson() {
        final CommunityPost communityPost = CommunityPost.builder() //
                .boardId("myBoard") //
                .tags(List.of("Docker", "Exasol", "Integration")) //
                .header("New tech article!") //
                .body("Here is my article!") //
                .build();
        assertThat(CommunityPostConverter.toJson(communityPost), equalTo("{\"data\":" //
                + "{\"subject\":\"New tech article!\"," //
                + "\"type\":\"message\"," //
                + "\"body\":\"Here is my article!\"," //
                + "\"board\":{\"id\":\"myBoard\"}," //
                + "\"content_workflow_action\":{\"workflow_action\":\"submit_for_review\"}," //
                + "\"teaser\":\"Find out how it can help you.\"," //
                + "\"tags\":{\"items\":[{\"text\":\"Docker\"},{\"text\":\"Exasol\"},{\"text\":\"Integration\"}]}" //
                + "}" //
                + "}"));
    }
}