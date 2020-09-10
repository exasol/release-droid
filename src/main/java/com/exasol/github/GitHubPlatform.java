package com.exasol.github;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.kohsuke.github.*;

import com.exasol.AbstractPlatform;

/**
 * This class controls GitHub platform.
 */
public class GitHubPlatform extends AbstractPlatform {
    public static final String GITHUB_API_ENTRY_URL = "https://api.github.com/repos/";
    private final GHRepository repository;
    private final GitHubUser gitHubUser;

    /**
     * Create a new instance of {@link GitHubPlatform}.
     * 
     * @param platformName name of the platform
     * @param repository instance of {@link GHRepository}
     * @param gitHubUser GitHub user
     */
    public GitHubPlatform(final PlatformName platformName, final GHRepository repository, final GitHubUser gitHubUser) {
        super(platformName);
        this.repository = repository;
        this.gitHubUser = gitHubUser;
    }

    /**
     * Create a new GitHub release.
     *
     * @param version version to release
     * @param releaseHeader header of the release
     * @param releaseBody body of the release letter
     */
    public void release(final String version, final String releaseHeader, final String releaseBody) {
        try {
            final GHRelease release = this.repository.createRelease(version).draft(true).body(releaseBody)
                    .name(releaseHeader).create();
            final String uploadUrl = release.getUploadUrl();
            uploadAssets(version, uploadUrl);
        } catch (final IOException exception) {
            throw new GitHubException(
                    "E-GH-PLF-1: GitHub connection problem happened during releasing a new tag. Please, try again later.",
                    exception);
        }
    }

    // [impl->dsn~upload-github-release-assets~1]
    private void uploadAssets(final String version, final String uploadUrl) {
        final URI uri = getAssetsUploadUri();
        final JSONObject body = new JSONObject();
        body.put("ref", "master");
        final JSONObject inputs = new JSONObject();
        inputs.put("version", version);
        inputs.put("upload_url", uploadUrl);
        body.put("inputs", inputs);
        final String json = body.toString();
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(uri) //
                .header("Accept", "application/vnd.github.v3+json") //
                .header("Authorization", "token " + this.gitHubUser.getToken()) //
                .header("Content-Type", "application/json") //
                .POST(HttpRequest.BodyPublishers.ofString(json)) //
                .build();
        final HttpClient build = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
        try {
            build.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (final IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubException("F-GH-PLF-1: Exception happened during uploading assets on the GitHub release.",
                    exception);
        }
    }

    // [impl->dsn~users-add-to-their-project-yml-files-that-helps-upload-deliverables~1]
    private URI getAssetsUploadUri() {
        final String uriString = GITHUB_API_ENTRY_URL + this.repository.getOwnerName() + "/" + this.repository.getName()
                + "/actions/workflows/upload_release_asset.yml/dispatches";
        try {
            return new URI(uriString);
        } catch (final URISyntaxException exception) {
            throw new IllegalArgumentException("F-GH-PLF-2: Cannot upload assets. Invalid URI format.", exception);
        }
    }

    /**
     * Get a set of closed issues' numbers.
     *
     * @return set of closed issues' numbers
     */
    public Set<Integer> getClosedTickets() {
        try {
            final List<GHIssue> closedIssues = this.repository.getIssues(GHIssueState.CLOSED);
            return closedIssues.stream().map(GHIssue::getNumber).collect(Collectors.toSet());
        } catch (final IOException exception) {
            throw new GitHubException(
                    "E-GH-PLF-2: Unable to retrieve a list of closed tickets. PLease, try again later.", exception);
        }
    }
}