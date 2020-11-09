package com.exasol.releaserobot.github;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.stream.Collectors;

import org.kohsuke.github.*;

import com.exasol.releaserobot.repository.GitRepositoryException;
import com.exasol.releaserobot.repository.maven.MavenRepository;
import com.exasol.releaserobot.usecases.Repository;

/**
 * Implements an adapter to interact with Github.
 */
public class GithubAPIAdapter implements GithubGateway {
    private static final String GITHUB_API_ENTRY_URL = "https://api.github.com/repos/";
    private final Map<String, GHRepository> repositories;
    private final GitHubUser gitHubUser;

    /**
     * Create a new instance of {@link GithubAPIAdapter}.
     *
     * @param gitHubUser instance of {@link GitHubUser}
     */
    public GithubAPIAdapter(final GitHubUser gitHubUser) {
        this.gitHubUser = gitHubUser;
        this.repositories = new HashMap<>();
    }

    private GHRepository createGHRepository(final String repositoryFullName, final GitHubUser user)
            throws GitHubException {
        try {
            final GitHub gitHub = GitHub.connect(user.getUsername(), user.getToken());
            return gitHub.getRepository(repositoryFullName);
        } catch (final IOException exception) {
            throw wrapGitHubException(repositoryFullName, exception);
        }
    }

    private GitHubException wrapGitHubException(final String repositoryName, final IOException exception) {
        final String originalMessage = exception.getMessage();
        final String newMessage;
        if (originalMessage.contains("Not Found")) {
            newMessage = "Repository '" + repositoryName
                    + "' not found. The repository doesn't exist or the user doesn't have permissions to see it.";
        } else if (originalMessage.contains("Bad credentials")) {
            newMessage = "A GitHub account with specified username and password doesn't exist.";
        } else {
            newMessage = originalMessage;
        }
        return new GitHubException("E-GH-1: " + newMessage, exception);
    }

    @Override
    public String createGithubRelease(final String repositoryFullName, final GitHubRelease gitHubRelease)
            throws GitHubException {
        try {
            final GHRelease ghRelease = this.getRepository(repositoryFullName)//
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

    private GHRepository getRepository(final String repositoryFullName) throws GitHubException {
        if (!this.repositories.containsKey(repositoryFullName)) {
            this.repositories.put(repositoryFullName, this.createGHRepository(repositoryFullName, this.gitHubUser));
        }
        return this.repositories.get(repositoryFullName);
    }

    @Override
    public Set<Integer> getClosedTickets(final String repositoryFullName) throws GitHubException {
        try {
            final List<GHIssue> closedIssues = this.getRepository(repositoryFullName).getIssues(GHIssueState.CLOSED);
            return closedIssues.stream().filter(ghIssue -> !ghIssue.isPullRequest()).map(GHIssue::getNumber)
                    .collect(Collectors.toSet());
        } catch (final IOException exception) {
            throw new GitHubException("F-RR-GH-4: Unable to retrieve a list of closed tickets on the GitHub.",
                    exception);
        }
    }

    @Override
    public Optional<String> getLatestTag(final String repositoryFullName) throws GitHubException {
        try {
            final GHRelease release = this.getRepository(repositoryFullName).getLatestRelease();
            return (release == null) ? Optional.empty() : Optional.of(release.getTagName());
        } catch (final IOException exception) {
            throw new GitRepositoryException(
                    "E-RR-GH-5: GitHub connection problem happened during retrieving the latest release.", exception);
        }
    }

    @Override
    public Repository getRepositoryWithUserSpecifiedBranch(final String repositoryFullName, final String branchName)
            throws GitHubException {
        return new MavenRepository(this.getRepository(repositoryFullName), branchName, repositoryFullName,
                getLatestTag(repositoryFullName));
    }

    @Override
    public Repository getRepositoryWithDefaultBranch(final String repositoryFullName) throws GitHubException {
        final GHRepository repository = this.getRepository(repositoryFullName);
        return new MavenRepository(repository, repository.getDefaultBranch(), repositoryFullName,
                getLatestTag(repositoryFullName));
    }

    @Override
    public void executeWorkflow(final String repositoryFullName, final String workflowName, final String payload)
            throws GitHubException {
        final URI workflowURI = this.getWorkflowURI(repositoryFullName, workflowName);
        this.sendGitHubRequest(workflowURI, payload);
    }

    private URI getWorkflowURI(final String repositoryFullName, final String workflowName) throws GitHubException {
        final String uriString = GITHUB_API_ENTRY_URL + repositoryFullName + "/actions/workflows/" + workflowName
                + "/dispatches";
        try {
            return new URI(uriString);
        } catch (final URISyntaxException exception) {
            throw new GitHubException("F-RR-GH-1: Cannot access a '" + workflowName + "' workflow. Invalid URI format.",
                    exception);
        }
    }

    private void sendGitHubRequest(final URI uri, final String json) throws GitHubException {
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(uri) //
                .header("Accept", "application/vnd.github.v3+json") //
                .header("Authorization", "token " + this.gitHubUser.getToken()) //
                .header("Content-Type", "application/json") //
                .POST(HttpRequest.BodyPublishers.ofString(json)) //
                .build();
        final HttpClient build = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
        try {
            final HttpResponse<String> response = build.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response);
        } catch (final IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubException("F-RR-GH-2: Exception happened during sending an HTTP request to the GitHub.",
                    exception);
        }
    }

    private void validateResponse(final HttpResponse<String> response) throws GitHubException {
        if ((response.statusCode() < HttpURLConnection.HTTP_OK)
                || (response.statusCode() >= HttpURLConnection.HTTP_MULT_CHOICE)) {
            throw new GitHubException("F-RR-GH-6: An HTTP request failed. " + response.body());
        }
    }
}