package com.exasol.releasedroid.github;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.kohsuke.github.*;

import com.exasol.releasedroid.repository.RepositoryException;
import com.exasol.releasedroid.repository.maven.MavenRepository;
import com.exasol.releasedroid.usecases.Repository;

/**
 * Implements an adapter to interact with Github.
 */
public class GithubAPIAdapter implements GithubGateway {
    private static final Logger LOGGER = Logger.getLogger(GithubAPIAdapter.class.getName());
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
            throw new RepositoryException(
                    "F-RR-GH-5: GitHub connection problem happened during retrieving the latest release.", exception);
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
        final String workflowUriPrefix = GITHUB_API_ENTRY_URL + repositoryFullName + "/actions/workflows/"
                + workflowName;
        final URI uri = createUriFromString(workflowUriPrefix + "/dispatches");
        final HttpResponse<String> response = sendGitHubPostRequest(uri, payload);
        validateResponse(response);
        logMessage(workflowName);
        final String workflowConclusion = getWorkflowConclusion(workflowUriPrefix);
        validateWorkflowConclusion(workflowConclusion);
    }

    private void logMessage(final String workflowName) {
        LOGGER.info(
                "A GitHub workflow '" + workflowName + "' has started. The Release Droid is monitoring its progress. "
                        + "This can take from a few minutes to a couple of hours depending on the build.");
    }

    private URI createUriFromString(final String uriString) throws GitHubException {
        try {
            return new URI(uriString);
        } catch (final URISyntaxException exception) {
            throw new GitHubException("F-RR-GH-1: Invalid URI: " + uriString, exception);
        }
    }

    private HttpResponse<String> sendGitHubPostRequest(final URI uri, final String body) throws GitHubException {
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(uri) //
                .header("Accept", "application/vnd.github.v3+json") //
                .header("Authorization", "token " + this.gitHubUser.getToken()) //
                .header("Content-Type", "application/json") //
                .POST(HttpRequest.BodyPublishers.ofString(body)) //
                .build();
        return sendGitHubRequest(request);
    }

    private HttpResponse<String> sendGitHubRequest(final HttpRequest request) throws GitHubException {
        final HttpClient build = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
        try {
            return build.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (final IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubException("F-RR-GH-2: Exception happened during sending an HTTP request to the GitHub.",
                    exception);
        }
    }

    private void validateResponse(final HttpResponse<String> response) throws GitHubException {
        if ((response.statusCode() < HttpURLConnection.HTTP_OK)
                || (response.statusCode() >= HttpURLConnection.HTTP_MULT_CHOICE)) {
            throw new GitHubException("F-RR-GH-6: An executing workflow HTTP request failed. " + response.body());
        }
    }

    private String getWorkflowConclusion(final String workflowUriPrefix) throws GitHubException {
        int minutesPassed = 0;
        while (true) {
            final int minutes = getMinutes(minutesPassed);
            minutesPassed += minutes;
            waitMinutes(minutes);
            final URI uri = createUriFromString(workflowUriPrefix + "/runs");
            final HttpResponse<String> response = sendGitHubGetRequest(uri);
            final JSONObject lastRun = new JSONObject(response.body()).getJSONArray("workflow_runs").getJSONObject(0);
            final boolean actionCompleted = !lastRun.isNull("conclusion");
            if (actionCompleted) {
                return lastRun.getString("conclusion");
            }
        }
    }

    // The fastest release takes 1-2 minutes, the slowest 1 hour and more.
    // We send 1 request per minute first 10 minutes and then 1 request per 5 minutes not to exceed the GitHub request
    // limits.
    private int getNextResultCheckDelayInMinutes(final int minutesPassed) {
        return minutesPassed < 10 ? 1 : 5;
    }

    private void waitMinutes(final int minutes) {
        try {
            Thread.sleep(60000L * minutes);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private HttpResponse<String> sendGitHubGetRequest(final URI uri) throws GitHubException {
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(uri) //
                .header("Accept", "application/vnd.github.v3+json") //
                .header("Authorization", "token " + this.gitHubUser.getToken()) //
                .header("Content-Type", "application/json") //
                .GET() //
                .build();
        return sendGitHubRequest(request);
    }

    private void validateWorkflowConclusion(final String workflowConclusion) throws GitHubException {
        if (!workflowConclusion.equals("success")) {
            throw new GitHubException(
                    "E-RR-GH-1: Workflow run failed. Please check the action logs on the GitHub to analyze the problem. Run result: "
                            + workflowConclusion);
        }
    }
}
