package com.exasol.github;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.*;

import org.json.JSONObject;
import org.kohsuke.github.*;

/**
 * An abstract base of {@link GitHubRepository}.
 */
public abstract class AbstractGitHubRepository implements GitHubRepository {
    private static final String CHANGELOG_FILE_PATH = "doc/changes/changelog.md";
    private final GHRepository repository;
    private final String oauthAccessToken;
    protected Map<String, String> filesCache = new HashMap<>();

    /**
     * A base constructor.
     * 
     * @param oauthAccessToken GitHub oauth Acess Token
     * @param repository an instance of {@link GHRepository}
     */
    protected AbstractGitHubRepository(final GHRepository repository, final String oauthAccessToken) {
        this.repository = repository;
        this.oauthAccessToken = oauthAccessToken;
    }

    @Override
    public Optional<String> getLatestReleaseVersion() {
        try {
            final GHRelease release = this.repository.getLatestRelease();
            return release == null ? Optional.empty() : Optional.of(release.getTagName());
        } catch (final IOException exception) {
            throw new GitHubException("GitHub connection problem happened during retrieving the latest release. "
                    + "Please, try again later. Cause: " + exception.getMessage(), exception);
        }
    }

    /**
     * Get a content of any file of this repository.
     *
     * @param filePath path of the file as a String
     * @return content as a string
     */
    protected String getSingleFileContentAsString(final String filePath) {
        try {
            final GHContent content = this.repository.getFileContent(filePath);
            return content.getContent();
        } catch (final IOException exception) {
            throw new GitHubException(
                    "Cannot find or read the file '" + filePath + "' in the repository " + this.repository.getName()
                            + ". Please add this file according to the User Guide. Cause: " + exception.getMessage(),
                    exception);
        }
    }

    @Override
    public String getChangelogFile() {
        if (!this.filesCache.containsKey(CHANGELOG_FILE_PATH)) {
            this.filesCache.put(CHANGELOG_FILE_PATH, getSingleFileContentAsString(CHANGELOG_FILE_PATH));
        }
        return this.filesCache.get(CHANGELOG_FILE_PATH);
    }

    @Override
    public String getChangesFile() {
        final String changesFileName = "doc/changes/changes-" + getVersion() + ".md";
        if (!this.filesCache.containsKey(changesFileName)) {
            this.filesCache.put(changesFileName, getSingleFileContentAsString(changesFileName));
        }
        return this.filesCache.get(changesFileName);
    }

    @Override
    public void release(final String name, final String releaseLetter) {
        try {
            final GHRelease release = this.repository.createRelease(getVersion()).draft(true).body(releaseLetter)
                    .name(name).create();
            final String uploadUrl = release.getUploadUrl();
            uploadAssets(uploadUrl);
        } catch (final IOException exception) {
            throw new GitHubException("GitHub connection problem happened during releasing a new tag. "
                    + "Please, try again later. Cause: " + exception.getMessage(), exception);
        }
    }

    private void uploadAssets(final String uploadUrl) {
        final URI uri = getAssetsUploadUri();
        final JSONObject body = new JSONObject();
        body.put("ref", "master");
        final JSONObject inputs = new JSONObject();
        inputs.put("version", getVersion());
        inputs.put("upload_url", uploadUrl);
        body.put("inputs", inputs);
        final String json = body.toString();
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(uri) //
                .header("Accept", "application/vnd.github.v3+json") //
                .header("Authorization", "token " + this.oauthAccessToken) //
                .header("Content-Type", "application/json") //
                .POST(HttpRequest.BodyPublishers.ofString(json)) //
                .build();
        final HttpClient build = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
        try {
            build.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (final IOException | InterruptedException exception) {
            throw new GitHubException("Exception happened during uploading assets on the GitHub release. Cause: "
                    + exception.getMessage(), exception);
        }
    }

    private URI getAssetsUploadUri() {
        final String uriString = "https://api.github.com/repos/" + this.repository.getOwnerName() + "/"
                + this.repository.getName() + "/actions/workflows/upload_release_asset.yml/dispatches";
        try {
            return new URI(uriString);
        } catch (final URISyntaxException exception) {
            throw new IllegalArgumentException("Cannot upload assets. Invalid URI format.", exception);
        }
    }
}