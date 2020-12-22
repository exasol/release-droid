package com.exasol.releasedroid.github;

import java.io.InputStream;
import java.util.Set;

/**
 * Gateway for interacting with Github.
 */
public interface GithubGateway {
    /**
     * Executes a GitHub workflow by a workflow name.
     *
     * @param repositoryName fully qualified name of the repository
     * @param workflowName   name of a workflow
     * @param payload        the payload in json format
     * @throws GitHubException when some problems occur
     */
    public void executeWorkflow(String repositoryName, String workflowName, String payload) throws GitHubException;

    /**
     * Make a GitHub release.
     * 
     * @param repositoryName fully qualified name of the repository
     * @param gitHubRelease  instance of {@link GitHubRelease} with release information
     * @return URl for attaching assets to the release as a string
     * @throws GitHubException when some problems occur
     */
    public String createGithubRelease(String repositoryName, GitHubRelease gitHubRelease) throws GitHubException;

    /**
     * Get a {@link Set} of closed tickets' numbers.
     *
     * @param repositoryName fully qualified name of the repository
     * @return set of closed tickets' numbers*
     * @throws GitHubException when some problems occur
     */
    public Set<Integer> getClosedTickets(String repositoryName) throws GitHubException;

    /**
     * Get latest tag.
     *
     * @param repositoryName fully qualified name of the repository
     * @return latest tag
     * @throws GitHubException when some problems occur
     */
    public String getLatestTag(String repositoryName) throws GitHubException;

    /**
     * Get a default branch of the repository.
     * 
     * @param repositoryName fully qualified name of the repository
     * @return default branch name
     * @throws GitHubException when some problems occur
     */
    public String getDefaultBranch(String repositoryName) throws GitHubException;

    /**
     * Get a content of a file by path.
     *
     * @param repositoryName fully qualified name of the repository
     * @param branchName     branch to retrieve the content from
     * @param filePath       path to a file
     * @return file content
     */
    public InputStream getFileContent(String repositoryName, String branchName, String filePath) throws GitHubException;

    /**
     * Update a single file content.
     *
     * @param repositoryName fully qualified name of the repository
     * @param branchName     branch to update the file on
     * @param filePath       path to a file
     * @param newContent     new file content
     * @param commitMessage  message to add to a commit
     */
    public void updateFileContent(String repositoryName, String branchName, String filePath, String newContent,
            String commitMessage) throws GitHubException;
}