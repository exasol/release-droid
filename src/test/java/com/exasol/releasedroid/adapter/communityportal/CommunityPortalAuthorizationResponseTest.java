package com.exasol.releasedroid.adapter.communityportal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommunityPortalAuthorizationResponseTest {
    @Test
    void testExtractTokenFromResponseBody() {
        final String body = "<response status=\"success\">" //
                + " <value type=\"string\">some_token.</value>" //
                + "</response>";
        final CommunityPortalAuthorizationResponse response = CommunityPortalAuthorizationResponse
                .createCommunityPortalAuthorizationResponse(body);
        assertAll(() -> assertThat(response.getToken().orElseThrow(), equalTo("some_token.")), //
                () -> assertTrue(response.isStatusOk()), //
                () -> assertTrue(response.getErrorMessage().isEmpty()) //
        );
    }

    @Test
    void testExtractErrorFromResponseBody() {
        final String body = "<response status=\"error\">" //
                + "  <error code=\"302\">" //
                + "    <message>" //
                + "      User authentication failed." //
                + "    </message>" //
                + "  </error>" //
                + "</response>";
        final CommunityPortalAuthorizationResponse response = CommunityPortalAuthorizationResponse
                .createCommunityPortalAuthorizationResponse(body);
        assertAll(() -> assertTrue(response.getToken().isEmpty()), //
                () -> assertFalse(response.isStatusOk()), //
                () -> assertThat(response.getErrorMessage().orElseThrow(), equalTo("User authentication failed.")) //
        );
    }
}