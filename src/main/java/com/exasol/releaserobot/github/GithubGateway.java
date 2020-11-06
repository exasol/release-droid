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
    public void executeWorkflow(String repositoryFullName, String workflowName, String payload) throws GitHubException;

    /**
     * Make a GitHub release.
     * 
     * @param repositoryFullName fully qualified name of the repository
     * @param gitHubRelease      instance of {@link GitHubRelease} with release information
     * @return URl for attaching assets to the release as a string
     * @throws GitHubException when some problems occur
     */
    public String createGithubRelease(String repositoryFullName, GitHubRelease gitHubRelease) throws GitHubException;

    /**
     * Get a {@link Set} of closed tickets' numbers.
     *
     * @param repositoryFullName fully qualified name of the repository
     * @return set of closed tickets' numbers*
     * @throws GitHubException when some problems occur
     */
    public Set<Integer> getClosedTickets(String repositoryFullName) throws GitHubException;

    /**
     * Get latest tag.
     *
     * @param repositoryFullName fully qualified name of the repository
     * @return latest tag
     * @throws GitHubException when some problems occur
     */
    public Optional<String> getLatestTag(String repositoryFullName) throws GitHubException;

    /**
     * Get a repository branch.
     *
     * @param repositoryFullName fully qualified name of the repository
     * @param branchName         branch name
     * @return instance of {@link Repository}
     * @throws GitHubException when some problems occur
     */
    public Repository getRepositoryWithUserSpecifiedBranch(String repositoryFullName, String branchName)
            throws GitHubException;

    /**
     * Get a default repository branch.
     *
     * @param repositoryFullName fully qualified name of the repository*
     * @return instance of {@link Repository}
     * @throws GitHubException when some problems occur
     */
    public Repository getRepositoryWithDefaultBranch(String repositoryFullName) throws GitHubException;
}