package com.exasol.releasedroid.adapter.communityportal;

import static com.exasol.releasedroid.adapter.communityportal.CommunityPortalConstants.EXASOL_COMMUNITY_PORTAL_URL;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.github.User;

/**
 * Implements an adapter to interact with Exasol Community Portal via API.
 */
public class CommunityPortalAPIAdapter implements CommunityPortalGateway {
    private final User user;

    /**
     * Create a new instance of {@link CommunityPortalAPIAdapter}.
     * 
     * @param user user with valid Community Portal credentials
     */
    public CommunityPortalAPIAdapter(final User user) {
        this.user = user;
    }

    @Override
    // [impl->dsn~create-new-release-announcement-on-exasol-community-portal~1]
    public void sendDraftPost(final CommunityPost communityPost) throws CommunityPortalException {
        final String token = getAuthenticationToken();
        createPost(CommunityPostConverter.toJson(communityPost), token);
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
                .POST(credentialsFormData(this.user)) //
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

    private void createPost(final String post, final String token) throws CommunityPortalException {
        final HttpRequest request = HttpRequest.newBuilder() //
                .header("li-api-session-key", token) //
                .uri(URI.create(EXASOL_COMMUNITY_PORTAL_URL + "api/2.0/messages")) //
                .POST(HttpRequest.BodyPublishers.ofString(post)) //
                .build();
        sendRequest(request);
    }

    private void validateResponse(final HttpResponse<String> response) throws CommunityPortalException {
        if (response.statusCode() != 200) {
            throw new CommunityPortalException(ExaError.messageBuilder("E-RD-CP-4") //
                    .message("The response from the Exasol Community Portal had a bad status: {{statusCode}}.",
                            response.statusCode()) //
                    .toString());
        }
    }

    private HttpRequest.BodyPublisher credentialsFormData(final User user) {
        final String formData = encode("user.login") + "=" + encode(user.getUsername()) //
                + "&" + encode("user.password") + "=" + encode(user.getPassword());
        return HttpRequest.BodyPublishers.ofString(formData);
    }

    private String encode(final String string) {
        return URLEncoder.encode(string, UTF_8);
    }
}