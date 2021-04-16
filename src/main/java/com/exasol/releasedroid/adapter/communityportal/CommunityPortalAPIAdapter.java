package com.exasol.releasedroid.adapter.communityportal;

import java.io.IOException;
import java.io.StringReader;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.exasol.releasedroid.adapter.github.User;
import com.exasol.releasedroid.usecases.exception.ReleaseException;

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
    public void createDraftPost(final CommunityPost communityPost) {
        final String token = getAuthenticationToken();
        final String post = convertToJson(communityPost);
        createPost(post, token);
    }

    private String getAuthenticationToken() {
        final HttpResponse<String> response = getAuthorizationResponse();
        final Document document = loadXMLFromString(response.body());
        return document.getChildNodes().item(0).getChildNodes().item(1).getTextContent();
    }

    private HttpResponse<String> getAuthorizationResponse() {
        final Map<String, String> data = new HashMap<>();
        data.put("user.login", this.user.getUsername());
        data.put("user.password", this.user.getPassword());
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(URI.create("https://community.exasol.com/restapi/vc/authentication/sessions/login")) //
                .POST(buildFormDataFromMap(data)) //
                .build();
        final HttpClient build = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
        try {
            return build.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToJson(final CommunityPost communityPost) {
        final JSONObject board = new JSONObject();
        board.put("id", communityPost.getBoardId());
        final JSONObject contentWorkflowAction = new JSONObject();
        contentWorkflowAction.put("workflow_action", "save_draft");
        final JSONObject tags = new JSONObject();
        final JSONArray tagItems = new JSONArray();
        for (final String tag : communityPost.getTags()) {
            tagItems.put(new JSONObject().put("text", tag));
        }
        tags.put("items", tagItems);
        final JSONObject data = new JSONObject();
        data.put("type", "message");
        data.put("board", board);
        data.put("subject", communityPost.getHeader());
        data.put("body", communityPost.getBody());
        data.put("teaser", communityPost.getTeaser());
        data.put("tags", tags);
        data.put("content_workflow_action", contentWorkflowAction);
        final JSONObject body = new JSONObject();
        body.put("data", data);
        return body.toString();
    }

    private void createPost(final String post, final String token) {
        final HttpRequest request = HttpRequest.newBuilder() //
                .header("li-api-session-key", token) //
                .header("Content-Type", "application/json") //
                .uri(URI.create("https://community.exasol.com/api/2.0/messages")) //
                .POST(HttpRequest.BodyPublishers.ofString(post)) //
                .build();
        final HttpClient build = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
        try {
            final HttpResponse<String> response = build.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response);
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException();
        }
    }

    private void validateResponse(final HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw new RuntimeException("Bad response.");
        }
    }

    private HttpRequest.BodyPublisher buildFormDataFromMap(final Map<String, String> data) {
        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    private Document loadXMLFromString(final String xml) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
        final InputSource is = new InputSource(new StringReader(xml));
        try {
            return builder.parse(is);
        } catch (final SAXException | IOException e) {
            throw new ReleaseException(e);
        }
    }
}