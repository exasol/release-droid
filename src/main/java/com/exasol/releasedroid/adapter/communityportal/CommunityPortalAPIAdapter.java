package com.exasol.releasedroid.adapter.communityportal;

import static com.exasol.releasedroid.adapter.communityportal.CommunityPortalConstants.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.StringReader;
import java.net.*;
import java.net.http.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.PropertyReader;

import jakarta.json.*;

/**
 * Implements an adapter to interact with Exasol Community Portal via API.
 */
public class CommunityPortalAPIAdapter implements CommunityPortalGateway {
    private final PropertyReader propertyReader;

    /**
     * Create a new instance of {@link CommunityPortalAPIAdapter}.
     *
     * @param propertyReader property reader
     */
    public CommunityPortalAPIAdapter(final PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    @Override
    // [impl->dsn~create-new-release-announcement-on-exasol-community-portal~1]
    public String sendDraftPost(final CommunityPost communityPost) throws CommunityPortalException {
        final String token = getAuthenticationToken();
        return createPost(CommunityPostConverter.toJson(communityPost), token);
    }

    private String getAuthenticationToken() throws CommunityPortalException {
        final HttpResponse<String> response = getAuthorizationResponse();
        final var portalAuthorizationResponse = CommunityPortalAuthorizationResponse
                .createCommunityPortalAuthorizationResponse(response.body());
        return getAuthenticationToken(portalAuthorizationResponse);
    }

    private String getAuthenticationToken(final CommunityPortalAuthorizationResponse portalAuthorizationResponse)
            throws CommunityPortalException {
        if (portalAuthorizationResponse.isStatusOk()) {
            return portalAuthorizationResponse.getToken()
                    .orElseThrow(() -> new CommunityPortalException(ExaError.messageBuilder("E-RD-CP-1").message(
                            "The Exasol Community Portal authentication token was not parsed correctly or missing.")
                            .toString()));
        } else {
            throw new CommunityPortalException(ExaError.messageBuilder("E-RD-CP-2").message("{{message}}")
                    .parameter("message",
                            portalAuthorizationResponse.getErrorMessage().orElse("Error message is missing."))
                    .toString());
        }
    }

    private HttpResponse<String> getAuthorizationResponse() throws CommunityPortalException {
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(URI.create(EXASOL_COMMUNITY_PORTAL_URL + "restapi/vc/authentication/sessions/login")) //
                .header("Content-Type", "application/x-www-form-urlencoded ") //
                .POST(credentialsFormData()) //
                .build();
        return sendRequest(request);
    }

    private HttpResponse<String> sendRequest(final HttpRequest request) throws CommunityPortalException {
        final HttpClient build = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
        try {
            final HttpResponse<String> response = build.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response);
            return response;
        } catch (final IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CommunityPortalException(exception);
        }
    }

    private String createPost(final String post, final String token) throws CommunityPortalException {
        final HttpRequest request = HttpRequest.newBuilder() //
                .header("li-api-session-key", token) //
                .uri(URI.create(EXASOL_COMMUNITY_PORTAL_URL + "api/2.0/messages")) //
                .POST(HttpRequest.BodyPublishers.ofString(post)) //
                .build();
        final HttpResponse<String> response = sendRequest(request);
        return extractPostUrl(response.body());
    }

    private String extractPostUrl(final String body) {
        return json(body).getJsonObject("data").getString("view_href");
    }

    static JsonObject json(final String string) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(string))) {
            return jsonReader.readObject();
        }
    }

    private void validateResponse(final HttpResponse<String> response) throws CommunityPortalException {
        if (response.statusCode() != 200) {
            throw new CommunityPortalException(ExaError.messageBuilder("E-RD-CP-4") //
                    .message("The response from the Exasol Community Portal had a bad status: {{statusCode}}.",
                            response.statusCode()) //
                    .toString());
        }
    }

    private HttpRequest.BodyPublisher credentialsFormData() {
        final String username = this.propertyReader.readProperty(COMMUNITY_USERNAME_KEY, false);
        final String password = this.propertyReader.readProperty(COMMUNITY_PASSWORD_KEY, true);
        final String formData = encode("user.login") + "=" + encode(username) //
                + "&" + encode("user.password") + "=" + encode(password);
        return HttpRequest.BodyPublishers.ofString(formData);
    }

    private String encode(final String string) {
        return URLEncoder.encode(string, UTF_8);
    }
}