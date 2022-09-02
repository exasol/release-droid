package com.exasol.releasedroid.adapter.github;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;

/**
 * Gateway for interacting with Github.
 */
public interface GitHubGateway {

    /**
     * @param repositoryName fully qualified name of the repository
     * @param workflowName   name of a workflow
     * @return estimation for duration of the workflow
     */
    Estimation estimateDuration(final String repositoryName, final String workflowName);

    /**
     * Executes a GitHub workflow by a workflow name on the default branch.
     *
     * @param repositoryName fully qualified name of the repository
     * @param workflowName   name of a workflow
     * @param options        {@link WorkflowOptions} for workflow execution
     * @throws GitHubException when some problems occur
     */
    void executeWorkflow(String repositoryName, String workflowName, WorkflowOptions options) throws GitHubException;

    /**
     * Executes a GitHub workflow by a workflow name on the default branch and return logs.
     *
     * @param repositoryName fully qualified name of the repository
     * @param workflowName   name of a workflow
     * @param options        {@link WorkflowOptions} for workflow execution
     * @return logs as a string
     * @throws GitHubException when some problems occur
     */
    String executeWorkflowWithLogs(String repositoryName, String workflowName, WorkflowOptions options)
            throws GitHubException;

    /**
     * Make a GitHub release on the head of default branch.
     *
     * @param gitHubRelease instance of {@link GitHubRelease} with release information
     * @param progress      progress to track and report progress of current release process
     * @throws GitHubException when some problems occur
     * @return information about release, including its draft state and html url for editing the draft
     */
    GitHubReleaseInfo createGithubRelease(GitHubRelease gitHubRelease, Progress progress) throws GitHubException;

//    /**
//     * Create an additional tag pointing to the same commit as an already existing tag (aka. "version"), probably
//     * created by {@link #createGithubRelease}. This is required for releases including golang sources.
//     *
//     * @param repositoryName fully qualified name of the repository
//     * @param tag            existing tag reference, e.g. "1.2.3"
//     * @param alias          additional tag reference to create, e.g. "v1.2.3" or "subfolder/v1.2.3"
//     * @throws GitHubException in case of failure
//     */
//    void createTag(final String repositoryName, final String tag, final String alias) throws GitHubException;

    /**
     * Get a {@link Set} of closed tickets' numbers.
     *
     * @param repositoryName fully qualified name of the repository
     * @return set of closed tickets' numbers*
     * @throws GitHubException when some problems occur
     */
    Set<Integer> getClosedTickets(String repositoryName) throws GitHubException;

    /**
     * Get latest tag.
     *
     * @param repositoryName fully qualified name of the repository
     * @return latest tag
     * @throws GitHubException when some problems occur
     */
    String getLatestTag(String repositoryName) throws GitHubException;

    /**
     * Get a default branch of the repository.
     *
     * @param repositoryName fully qualified name of the repository
     * @return default branch name
     * @throws GitHubException when some problems occur
     */
    String getDefaultBranch(String repositoryName) throws GitHubException;

    /**
     * Get a content of a file by path.
     *
     * @param repositoryName fully qualified name of the repository
     * @param branchName     branch to retrieve the content from
     * @param filePath       path to a file
     * @return file content
     * @throws GitHubException when some problems occur
     */
    InputStream getFileContent(String repositoryName, String branchName, String filePath) throws GitHubException;

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
    void updateFileContent(String repositoryName, String branchName, String filePath, String newContent,
            String commitMessage) throws GitHubException;

    /**
     * Get a repository's primary programming language.
     *
     * @param repositoryName fully qualified name of the repository
     * @return repository primary language as a string
     * @throws GitHubException when some problems occur
     */
    String getRepositoryPrimaryLanguage(String repositoryName) throws GitHubException;

    /**
     * Get a list of artifact's ids that are not expired.
     *
     * @param repositoryName fully qualified name of the repository
     * @return list of artifact's ids
     * @throws GitHubException when some problems occur
     */
    List<Long> getRepositoryArtifactsIds(String repositoryName) throws GitHubException;

    /**
     * Download a GitHub artifact as a String.
     *
     * @param repositoryName fully qualified name of the repository
     * @param artifactId     id of the artifact to download the artifact from
     * @return artifact as a string
     * @throws GitHubException when some problems occur
     */
    String downloadArtifactAsString(String repositoryName, long artifactId) throws GitHubException;

    /**
     * Delete all artifacts from the repository.
     *
     * @param repositoryName fully qualified name of the repository
     * @throws GitHubException when some problems occur
     */
    void deleteAllArtifacts(String repositoryName) throws GitHubException;
}