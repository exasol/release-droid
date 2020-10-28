package com.exasol.releaserobot;

import java.net.URI;

import org.json.JSONObject;
import org.kohsuke.github.GHRepository;

import com.exasol.releaserobot.github.GitHubUser;

/**
 * This class controls Maven platform.
 */
public class MavenPlatform extends AbstractPlatform {
    private static final PlatformName PLATFORM_NAME = PlatformName.MAVEN;

    /**
     * Create a new instance of {@link MavenPlatform}.
     *
     * @param repository instance of {@link GHRepository}
     * @param gitHubUser GitHub user
     */
    public MavenPlatform(final GHRepository repository, final GitHubUser gitHubUser) {
        super(PLATFORM_NAME, repository, gitHubUser);
    }

    /**
     * Create a new Maven Central release.
     */
    public void makeNewMavenRelease() {
        final URI uri = getWorkflowUri("maven_central_release.yml");
        final JSONObject body = new JSONObject();
        body.put("ref", "master");
        final String json = body.toString();
        sendGitHubRequest(uri, json);
    }
}