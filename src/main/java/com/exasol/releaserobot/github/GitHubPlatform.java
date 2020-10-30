package com.exasol.releaserobot.github;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.exasol.releaserobot.Platform;

/**
 * This class controls GitHub platform.
 */
public class GitHubPlatform implements Platform {
    private final GithubGateway githubGateway;

    /**
     * Create a new instance of {@link GitHubPlatform}.
     *
     * @param githubGateway instance of {@link GithubGateway}
     */
    protected GitHubPlatform(final GithubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    /**
     * Create a new GitHub release.
     *
     * @throws GitHubException when release process fails
     * @param gitHubRelease {@link GitHubRelease} instance with information about the release
     */
    public void makeNewGitHubRelease(final GitHubRelease gitHubRelease) throws GitHubException {
        final String uploadUrl = this.githubGateway.createGithubRelease(gitHubRelease);
        for (final Map.Entry<String, String> asset : gitHubRelease.getAssets().entrySet()) {
            uploadAssets(uploadUrl, asset.getKey(), asset.getValue(), gitHubRelease.getDefaultBranchName());
        }
    }

    // [impl->dsn~upload-github-release-assets~1]
    // [impl->dsn~users-add-upload-definition-files-for-their-deliverables~1]
    private void uploadAssets(final String uploadUrl, final String assetName, final String assetPath,
            final String defaultBranchName) throws GitHubException {
        final URI uri = this.githubGateway.getWorkflowURI("github_release.yml");
        final JSONObject body = new JSONObject();
        body.put("ref", defaultBranchName);
        final JSONObject inputs = new JSONObject();
        inputs.put("upload_url", uploadUrl);
        inputs.put("asset_name", assetName);
        inputs.put("asset_path", assetPath);
        body.put("inputs", inputs);
        final String json = body.toString();
        this.githubGateway.sendGitHubRequest(uri, json);
    }

    /**
     * Get a set of closed issues' numbers.
     *
     * @return set of closed issues' numbers
     */
    public Set<Integer> getClosedTickets() {
        try {
            return this.githubGateway.getClosedTickets();
        } catch (final GitHubException exception) {
            throw new IllegalStateException(exception.getMessage(), exception);
        }
    }

    @Override
    public PlatformName getPlatformName() {
        return PlatformName.GITHUB;
    }
}