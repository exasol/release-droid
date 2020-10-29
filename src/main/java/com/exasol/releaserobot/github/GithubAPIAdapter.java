package com.exasol.releaserobot.github;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;

import com.exasol.releaserobot.GithubGateway;

public class GithubAPIAdapter implements GithubGateway {
	private static final String GITHUB_API_ENTRY_URL = "https://api.github.com/repos/";
	private final GHRepository repository;
    protected final GitHubUser gitHubUser;
    
	public GithubAPIAdapter(final  GHRepository repository,  final GitHubUser gitHubUser) {
		super();
		this.repository = repository;
		this.gitHubUser = gitHubUser;
	}

	@Override
	public URI getWorkflowURI(String workflowName) {
		final String uriString = GITHUB_API_ENTRY_URL + this.repository.getOwnerName() + "/" + this.repository.getName()
				+ "/actions/workflows/" + workflowName + "/dispatches";
		try {
			return new URI(uriString);
		} catch (final URISyntaxException exception) {
			throw new GitHubException(
					"F-RR-PLF-2: Cannot access a '" + workflowName + "' workflow. Invalid URI format.", exception);
		}
	}

	@Override
	public void sendGitHubRequest(URI uri, String json) {
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
			Thread.currentThread().interrupt();
			throw new GitHubException("F-RR-PLF-1: Exception happened during uploading assets on the GitHub release.",
					exception);
		}
	}

	@Override
	public String createGithubRelease(GitHubRelease gitHubRelease) throws IOException {
		 return this.repository //
                 .createRelease(gitHubRelease.getVersion()) //
                 .draft(true) //
                 .body(gitHubRelease.getReleaseLetter()) //
                 .name(gitHubRelease.getHeader()) //
                 .create().getUploadUrl();
	}

	@Override
	public Set<Integer> getClosedTickets() throws IOException {
		 final List<GHIssue> closedIssues = this.repository.getIssues(GHIssueState.CLOSED);
         return closedIssues.stream().filter(ghIssue -> !ghIssue.isPullRequest()).map(GHIssue::getNumber)
                 .collect(Collectors.toSet());
	}

}
