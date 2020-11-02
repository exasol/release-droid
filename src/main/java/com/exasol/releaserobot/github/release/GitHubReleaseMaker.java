package com.exasol.releaserobot.github.release;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.exasol.releaserobot.ReleaseMaker;
import com.exasol.releaserobot.github.*;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.ReleaseLetter;

/**
 * This class is responsible for releases on GitHub.
 */
public class GitHubReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(GitHubReleaseMaker.class.getName());
    private final GitBranchContent content;
    private final GithubGateway githubGateway;

    /**
     * Create a new {@link GitHubReleaseMaker}.
     * 
     * @param content       repository content to release
     * @param githubGateway instance of {@link GithubGateway}
     */
    public GitHubReleaseMaker(final GitBranchContent content, final GithubGateway githubGateway) {
        this.content = content;
        this.githubGateway = githubGateway;
    }

    @Override
    // [impl->dsn~create-new-github-release~1]
    // [impl->dsn~retrieve-github-release-header-from-release-letter~1]
    // [impl->dsn~retrieve-github-release-body-from-release-letter~1]
    public void makeRelease() throws GitHubException {
        LOGGER.fine("Releasing on GitHub.");
        final String version = this.content.getVersion();
        final ReleaseLetter releaseLetter = this.content.getReleaseLetter(version);
        final String body = releaseLetter.getBody().orElse("");
        final String header = releaseLetter.getHeader().orElse(version);
        final GitHubRelease release = GitHubRelease.builder().version(version).header(header).releaseLetter(body)
                .defaultBranchName(this.content.getBranchName()).assets(this.content.getDeliverables()).build();
        this.makeNewGitHubRelease(release);
    }

    private void makeNewGitHubRelease(final GitHubRelease gitHubRelease) throws GitHubException {
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
}