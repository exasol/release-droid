package com.exasol.github;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.Optional;

import org.json.JSONObject;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;

import com.exasol.GitRepository;
import com.exasol.GitRepositoryContent;

/**
 * A GitHub-based repository.
 */
public class GitHubGitRepository implements GitRepository {
    private static final String GITHUB_API_ENTRY_URL = "https://api.github.com/repos/";
    private final GHRepository repository;
    private final GitHubUser gitHubUser;

    /**
     * Create a new instance of {@link GitHubGitRepository}.
     * 
     * @param repository instance of {@link GHRepository}
     * @param gitHubUser user that stores GitHub credentials
     */
    public GitHubGitRepository(final GHRepository repository, final GitHubUser gitHubUser) {
        this.repository = repository;
        this.gitHubUser = gitHubUser;
    }

    @Override
    public Optional<String> getLatestReleaseTag() {
        try {
            final GHRelease release = this.repository.getLatestRelease();
            return release == null ? Optional.empty() : Optional.of(release.getTagName());
        } catch (final IOException exception) {
            throw new GitHubException("GitHub connection problem happened during retrieving the latest release. "
                    + "Please, try again later.", exception);
        }
    }

    @Override
    public void release(final String version, final String releaseLetter) {
        try {
            final GHRelease release = this.repository.createRelease(version).draft(true).body(releaseLetter)
                    .name(version).create();
            final String uploadUrl = release.getUploadUrl();
            uploadAssets(version, uploadUrl);
        } catch (final IOException exception) {
            throw new GitHubException(
                    "GitHub connection problem happened during releasing a new tag. Please, try again later.",
                    exception);
        }
    }

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
            throw new GitHubException("Exception happened during uploading assets on the GitHub release.", exception);
        }
    }

    private URI getAssetsUploadUri() {
        final String uriString = GITHUB_API_ENTRY_URL + this.repository.getOwnerName() + "/" + this.repository.getName()
                + "/actions/workflows/upload_release_asset.yml/dispatches";
        try {
            return new URI(uriString);
        } catch (final URISyntaxException exception) {
            throw new IllegalArgumentException("Cannot upload assets. Invalid URI format.", exception);
        }
    }

    @Override
    public GitRepositoryContent getRepositoryContent(final String branchName) {
        return GitHubRepositoryContentFactory.getInstance().getGitHubRepositoryContent(this.repository, branchName);
    }

    @Override
    public GitRepositoryContent getRepositoryContent() {
        return getRepositoryContent(this.repository.getDefaultBranch());
    }
}