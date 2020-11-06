package com.exasol.releaserobot.maven;

import java.net.URI;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.github.GithubGateway;
import com.exasol.releaserobot.repository.Branch;
import com.exasol.releaserobot.usecases.release.ReleaseMaker;

/**
 * This class is responsible for releases on Maven Central.
 */
public class MavenReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(MavenReleaseMaker.class.getName());
    private final GithubGateway githubGateway;

    /**
     * Create a new instance of {@link MavenReleaseMaker}.
     *
     * @param githubGateway instance of {@link GithubGateway}
     */
    public MavenReleaseMaker(final GithubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    @Override
    public void makeRelease(final Branch branch) throws GitHubException {
        LOGGER.fine("Releasing on Maven.");
        final URI uri = this.githubGateway.getWorkflowURI("maven_central_release.yml");
        final JSONObject body = new JSONObject();
        body.put("ref", branch.getBranchName());
        final String json = body.toString();
        this.githubGateway.sendGitHubRequest(uri, json);
    }
}
