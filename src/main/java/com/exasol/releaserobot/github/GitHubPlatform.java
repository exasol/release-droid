package com.exasol.releaserobot.github;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.kohsuke.github.*;

import com.exasol.releaserobot.AbstractPlatform;

/**
 * This class controls GitHub platform.
 */
public class GitHubPlatform extends AbstractPlatform {
    private static final PlatformName PLATFORM_NAME = PlatformName.GITHUB;

    /**
     * Create a new instance of {@link GitHubPlatform}.
     * 
     * @param repository instance of {@link GHRepository}
     * @param gitHubUser GitHub user
     */
    public GitHubPlatform(final GHRepository repository, final GitHubUser gitHubUser) {
        super(PLATFORM_NAME, repository, gitHubUser);
    }

    /**
     * Create a new GitHub release.
     *
     * @param gitHubRelease {@link GitHubRelease} instance with information about the release
     */
    public void makeNewGitHubRelease(final GitHubRelease gitHubRelease) {
        try {
            final GHRelease release = this.repository //
                    .createRelease(gitHubRelease.getVersion()) //
                    .draft(true) //
                    .body(gitHubRelease.getReleaseLetter()) //
                    .name(gitHubRelease.getHeader()) //
                    .create();
            final String uploadUrl = release.getUploadUrl();
            for (final Map.Entry<String, String> asset : gitHubRelease.getAssets().entrySet()) {
                uploadAssets(uploadUrl, asset.getKey(), asset.getValue());
            }
        } catch (final IOException exception) {
            throw new GitHubException(
                    "E-GH-PLF-1: GitHub connection problem happened during releasing a new tag. Please, try again later.",
                    exception);
        }
    }

    // [impl->dsn~upload-github-release-assets~1]
    // [impl->dsn~users-add-upload-definition-files-for-their-deliverables~1]
    private void uploadAssets(final String uploadUrl, final String assetName, final String assetPath) {
        final URI uri = getWorkflowUri("github_release.yml");
        final JSONObject body = new JSONObject();
        body.put("ref", "master");
        final JSONObject inputs = new JSONObject();
        inputs.put("upload_url", uploadUrl);
        inputs.put("asset_name", assetName);
        inputs.put("asset_path", assetPath);
        body.put("inputs", inputs);
        final String json = body.toString();
        sendGitHubRequest(uri, json);
    }

    /**
     * Get a set of closed issues' numbers.
     *
     * @return set of closed issues' numbers
     */
    public Set<Integer> getClosedTickets() {
        try {
            final List<GHIssue> closedIssues = this.repository.getIssues(GHIssueState.CLOSED);
            return closedIssues.stream().filter(ghIssue -> !ghIssue.isPullRequest()).map(GHIssue::getNumber)
                    .collect(Collectors.toSet());
        } catch (final IOException exception) {
            throw new GitHubException(
                    "E-GH-PLF-2: Unable to retrieve a list of closed tickets. PLease, try again later.", exception);
        }
    }
}