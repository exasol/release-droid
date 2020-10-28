package com.exasol.releaserobot;

import java.io.IOException;
import java.net.*;
import java.net.http.*;

import org.kohsuke.github.GHRepository;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.github.GitHubUser;

/**
 * An abstract base for classes implementing {@link Platform}. The platforms are currently using GitHub workflows.
 */
public abstract class AbstractPlatform implements Platform {
    public static final String GITHUB_API_ENTRY_URL = "https://api.github.com/repos/";
    private final PlatformName platformName;
    protected final GitHubUser gitHubUser;
    protected final GHRepository repository;

    /**
     * An abstract base constructor.
     * 
     * @param platformName name of the platform
     * @param repository   instance of {@link GHRepository}
     * @param gitHubUser   GitHub user
     */
    protected AbstractPlatform(final PlatformName platformName, final GHRepository repository,
            final GitHubUser gitHubUser) {
        this.platformName = platformName;
        this.repository = repository;
        this.gitHubUser = gitHubUser;
    }

    @Override
    public PlatformName getPlatformName() {
        return this.platformName;
    }

    protected URI getWorkflowUri(final String workflowName) {
        final String uriString = GITHUB_API_ENTRY_URL + this.repository.getOwnerName() + "/" + this.repository.getName()
                + "/actions/workflows/" + workflowName + "/dispatches";
        try {
            return new URI(uriString);
        } catch (final URISyntaxException exception) {
            throw new GitHubException(
                    "F-RR-PLF-2: Cannot access a '" + workflowName + "' workflow. Invalid URI format.", exception);
        }
    }

    protected void sendGitHubRequest(final URI uri, final String json) {
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
}