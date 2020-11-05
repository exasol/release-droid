package com.exasol.releaserobot.github;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import com.exasol.releaserobot.repository.Branch;

/**
 * Gateway for interacting with Github.
 */
public interface GithubGateway {
    /**
     * Get a GitHub workflow URI by a workflow name.
     *
     * @param workflowName name of a workflow
     * @return new {@link URI}
     * @throws GitHubException when some problems occur
     */
    public URI getWorkflowURI(String workflowName) throws GitHubException;

    /**
     * Send a POST HTTP request to the provided URI.
     *
     * @param uri  used-provided URI
     * @param json request body
     * @throws GitHubException when some problems occur
     */
    public void sendGitHubRequest(URI uri, String json) throws GitHubException;

    /**
     * Make a GitHub release.
     *
     * @param gitHubRelease instance of {@link GitHubRelease} with release information
     * @return URl for attaching assets to the release as a string
     * @throws GitHubException when some problems occur
     */
    public String createGithubRelease(GitHubRelease gitHubRelease) throws GitHubException;

    /**
     * Get a {@link Set} of closed tickets' numbers.
     *
     * @return set of closed tickets' numbers*
     * @throws GitHubException when some problems occur
     */
    public Set<Integer> getClosedTickets() throws GitHubException;

    /**
     * Get latest tag.
     * 
     * @return latest tag
     */
    public Optional<String> getLatestTag();

    /**
     * Get a repository branch.
     * 
     * @param branchName branch name
     * @return instance of {@link Branch}
     */
    public Branch getBranch(String branchName);

    /**
     * Get a default repository branch.
     *
     * @return instance of {@link Branch}
     */
    public Branch getDefaultBranch();
}