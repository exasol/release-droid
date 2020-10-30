package com.exasol.releaserobot;

import java.net.URI;

import org.json.JSONObject;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.github.GithubGateway;

/**
 * This class controls Maven platform.
 */
public class MavenPlatform implements Platform {
    private final GithubGateway githubGateway;

    /**
     * Create a new instance of {@link MavenPlatform}.
     *
     * @param githubGateway instance of {@link GithubGateway}
     */
    public MavenPlatform(final GithubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    /**
     * Create a new Maven Central release.
     * 
     * @throws GitHubException when release process fails
     */
    public void makeNewMavenRelease() throws GitHubException {
        final URI uri = this.githubGateway.getWorkflowURI("maven_central_release.yml");
        final JSONObject body = new JSONObject();
        body.put("ref", "master");
        final String json = body.toString();
        this.githubGateway.sendGitHubRequest(uri, json);
    }

    @Override
    public PlatformName getPlatformName() {
        return PlatformName.MAVEN;
    }
}