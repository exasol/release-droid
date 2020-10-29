package com.exasol.releaserobot;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import com.exasol.releaserobot.github.GitHubRelease;

public interface GithubGateway {

	URI getWorkflowURI(String workflowName);

	void sendGitHubRequest(URI uri, String json);

	String createGithubRelease(GitHubRelease gitHubRelease) throws IOException;

	Set<Integer> getClosedTickets() throws IOException;

}
