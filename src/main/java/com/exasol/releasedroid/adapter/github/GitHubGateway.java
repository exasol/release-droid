package com.exasol.releasedroid.adapter.github;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gateway for interacting with Github.
 */
public interface GitHubGateway {
    /**
     * Executes a GitHub workflow by a workflow name on the default branch.
     *
     * @param repositoryName fully qualified name of the repository
     * @param workflowName   name of a workflow
     * @param dispatches     map with the dispatches arguments. Can be empty
     * @throws GitHubException when some problems occur
     */
    public void executeWorkflow(String repositoryName, String workflowName, Map<String, Object> dispatches)
            throws GitHubException;

    /**
     * Executes a GitHub workflow by a workflow name on the default branch.
     *
     * @param repositoryName fully qualified name of the repository
     * @param workflowName   name of a workflow
     * @throws GitHubException when some problems occur
     */
    public void executeWorkflow(String repositoryName, String workflowName) throws GitHubException;

    /**
     * Executes a GitHub workflow by a workflow name on the default branch and return logs.
     *
     * @param repositoryName fully qualified name of the repository
     * @param workflowName   name of a workflow
     * @return logs as a string
     * @throws GitHubException when some problems occur
     */
    public String executeWorkflowWithLogs(String repositoryName, String workflowName) throws GitHubException;

    /**
     * Make a GitHub release on the head of default branch.
     * 
     * @param gitHubRelease instance of {@link GitHubRelease} with release information
     * @throws GitHubException when some problems occur
     */
    public void createGithubRelease(GitHubRelease gitHubRelease) throws GitHubException;

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
     * @throws GitHubException when some problems occur
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
     * @throws GitHubException when some problems occur
     */
    public void updateFileContent(String repositoryName, String branchName, String filePath, String newContent,
            String commitMessage) throws GitHubException;

    /**
     * Get a repository's primary programming language.
     * 
     * @param repositoryName fully qualified name of the repository
     * @return repository primary language as a string
     * @throws GitHubException when some problems occur
     */
    public String getRepositoryPrimaryLanguage(String repositoryName) throws GitHubException;

    /**
     * Get a list of artifact's ids that are not expired.
     *
     * @param repositoryName fully qualified name of the repository
     * @return list of artifact's ids
     * @throws GitHubException when some problems occur
     */
    public List<Long> getRepositoryArtifactsIds(String repositoryName) throws GitHubException;

    /**
     * Download a GitHub artifact as a String.
     * 
     * @param repositoryName fully qualified name of the repository
     * @param artifactId     id of the artifact to download the artifact from
     * @return artifact as a string
     * @throws GitHubException when some problems occur
     */
    public String downloadArtifactAsString(String repositoryName, long artifactId) throws GitHubException;

    /**
     * Delete all artifacts from the repository.
     *
     * @param repositoryName fully qualified name of the repository
     * @throws GitHubException when some problems occur
     */
    public void deleteAllArtifacts(String repositoryName) throws GitHubException;
}