package com.exasol.releaserobot.maven.release;

import java.net.URI;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.exasol.releaserobot.ReleaseMaker;
import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.github.GithubGateway;
import com.exasol.releaserobot.maven.MavenPlatform;
import com.exasol.releaserobot.report.ReleaseReport;
import com.exasol.releaserobot.repository.GitBranchContent;

/**
 * This class is responsible for releases on Maven Central.
 */
public class MavenReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(MavenReleaseMaker.class.getName());
    private final GitBranchContent content;
    private final GithubGateway githubGateway;

	/**
	 * Create a new instance of {@link MavenReleaseMaker}.
	 *
	 * @param content       repository content
	 * @param platform      instance of {@link MavenPlatform}
	 * @param releaseReport instance of {@link ReleaseReport}
	 */
	public MavenReleaseMaker(final GitBranchContent content, final GithubGateway githubGateway) {
		this.content = content;
		this.githubGateway = githubGateway;
	}

    @Override
    public void makeRelease() throws GitHubException {
        LOGGER.fine("Releasing on Maven.");
        final URI uri = this.githubGateway.getWorkflowURI("maven_central_release.yml");
        final JSONObject body = new JSONObject();
        body.put("ref", this.content.getBranchName());
        final String json = body.toString();
        this.githubGateway.sendGitHubRequest(uri, json);
    }
}