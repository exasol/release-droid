package com.exasol.releaserobot.github;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.kohsuke.github.*;

/**
 * Implements a GitHun related functionality.
 */
public class GithubAPIAdapter implements GithubGateway {
    private static final String GITHUB_API_ENTRY_URL = "https://api.github.com/repos/";
    private final GHRepository repository;
    private final GitHubUser gitHubUser;

    /**
     * Create a new instance of {@link GithubAPIAdapter}.
     * 
     * @param repository instance of {@link GHRepository}
     * @param gitHubUser instance of {@link GitHubUser}
     */
    public GithubAPIAdapter(final GHRepository repository, final GitHubUser gitHubUser) {
        this.repository = repository;
        this.gitHubUser = gitHubUser;
    }

    @Override
    public URI getWorkflowURI(final String workflowName) throws GitHubException {
        final String uriString = GITHUB_API_ENTRY_URL + this.repository.getOwnerName() + "/" + this.repository.getName()
                + "/actions/workflows/" + workflowName + "/dispatches";
        try {
            return new URI(uriString);
        } catch (final URISyntaxException exception) {
            throw new GitHubException("F-RR-GH-1: Cannot access a '" + workflowName + "' workflow. Invalid URI format.",
                    exception);
        }
    }

    @Override
    public void sendGitHubRequest(final URI uri, final String json) throws GitHubException {
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
            throw new GitHubException("F-RR-GH-2: Exception happened during sending an HTTP request to the GitHub.",
                    exception);
        }
    }

    @Override
    public String createGithubRelease(final GitHubRelease gitHubRelease) throws GitHubException {
        try {
            final GHRelease ghRelease = this.repository //
                    .createRelease(gitHubRelease.getVersion()) //
                    .draft(true) //
                    .body(gitHubRelease.getReleaseLetter()) //
                    .name(gitHubRelease.getHeader()) //
                    .create();
            return ghRelease.getUploadUrl();
        } catch (final IOException exception) {
            throw new GitHubException("F-RR-GH-3: Exception happened during releasing a new tag on the GitHub.",
                    exception);
        }
    }

    @Override
    public Set<Integer> getClosedTickets() throws GitHubException {
        try {
            final List<GHIssue> closedIssues = this.repository.getIssues(GHIssueState.CLOSED);
            return closedIssues.stream().filter(ghIssue -> !ghIssue.isPullRequest()).map(GHIssue::getNumber)
                    .collect(Collectors.toSet());
        } catch (final IOException exception) {
            throw new GitHubException("F-RR-GH-4: Unable to retrieve a list of closed tickets on the GitHub.",
                    exception);
        }
    }
}