package com.exasol.releaserobot.maven;

import java.util.logging.Logger;

import org.json.JSONObject;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.github.GithubGateway;
import com.exasol.releaserobot.usecases.Repository;
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
    // [impl->dsn~create-new-maven-release~1]
    public void makeRelease(final Repository repository) throws GitHubException {
        LOGGER.fine("Releasing on Maven.");
        final JSONObject body = new JSONObject();
        body.put("ref", repository.getBranchName());
        final String json = body.toString();
        this.githubGateway.executeWorkflow(repository.getFullName(), "maven_central_release.yml", json);
    }
}
