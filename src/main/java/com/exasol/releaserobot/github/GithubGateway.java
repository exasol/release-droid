package com.exasol.releaserobot.github;

import java.util.Optional;
import java.util.Set;

import com.exasol.releaserobot.usecases.Repository;

/**
 * Gateway for interacting with Github.
 */
public interface GithubGateway {
    /**
     * Executes a GitHub workflow by a workflow name.
     *
     * @param repositoryFullName fully qualified name of the repository
     * @param workflowName       name of a workflow
     * @param payload            the payload in json format
     * @throws GitHubException when some problems occur
     */
    void executeWorkflow(String repositoryFullName, String workflowName, String payload) throws GitHubException;

    /**
     * Make a GitHub release.
     *
     * @param gitHubRelease instance of {@link GitHubRelease} with release information
     * @return URl for attaching assets to the release as a string
     * @throws GitHubException when some problems occur
     */
    String createGithubRelease(String repositoryFullName, GitHubRelease gitHubRelease) throws GitHubException;

    /**
     * Get a {@link Set} of closed tickets' numbers.
     *
     * @return set of closed tickets' numbers*
     * @throws GitHubException when some problems occur
     */
    Set<Integer> getClosedTickets(String repositoryFullName) throws GitHubException;

    /**
     * Get latest tag.
     *
     * @return latest tag
     * @throws GitHubException
     */
    Optional<String> getLatestTag(String repositoryFullName) throws GitHubException;

    /**
     * Get a repository branch.
     *
     * @param branchName branch name
     * @return instance of {@link Repository}
     * @throws GitHubException
     */
    Repository getBranch(String repositoryFullName, String branchName) throws GitHubException;

    /**
     * Get a default repository branch.
     *
     * @return instance of {@link Repository}
     * @throws GitHubException
     */
    Repository getDefaultBranch(String repositoryFullName) throws GitHubException;

}
