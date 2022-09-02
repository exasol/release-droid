package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.github.GitHubConstants.GITHUB_UPLOAD_ASSETS_WORKFLOW;
import static com.exasol.releasedroid.formatting.Colorizer.formatLink;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import org.kohsuke.github.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;
import com.exasol.releasedroid.usecases.exception.RepositoryException;

/**
 * Implements an adapter to interact with Github.
 */
public class GitHubAPIAdapter implements GitHubGateway {
    private static final Logger LOGGER = Logger.getLogger(GitHubAPIAdapter.class.getName());
    private final Map<String, GHRepository> repositories = new HashMap<>();
    private final GitHubConnector gitHubConnector;

    /**
     * Create a new instance of {@link GitHubAPIAdapter}.
     *
     * @param gitHubConnector GitHub connector
     */
    public GitHubAPIAdapter(final GitHubConnector gitHubConnector) {
        this.gitHubConnector = gitHubConnector;
    }

    private GHRepository getRepository(final String repositoryName) throws GitHubException {
        if (!this.repositories.containsKey(repositoryName)) {
            this.repositories.put(repositoryName, this.createGHRepository(repositoryName));
        }
        return this.repositories.get(repositoryName);
    }

    private GHRepository createGHRepository(final String repositoryName) throws GitHubException {
        try {
            return this.gitHubConnector.connectToGitHub().getRepository(repositoryName);
        } catch (final IOException exception) {
            throw wrapGitHubException(repositoryName, exception);
        }
    }

    private GitHubException wrapGitHubException(final String repositoryName, final IOException exception) {
        final String originalMessage = exception.getMessage();
        if (originalMessage.contains("Not Found")) {
            return new GitHubException(ExaError.messageBuilder("E-RD-GH-1") //
                    .message(
                            "Repository {{repositoryName}} not found. "
                                    + "The repository doesn't exist or the user doesn't have permissions to see it.",
                            repositoryName)
                    .toString(), exception);
        } else if (originalMessage.contains("Bad credentials")) {
            return new GitHubException(ExaError.messageBuilder("E-RD-GH-13") //
                    .message("A GitHub account with specified username and password doesn't exist.").toString(),
                    exception);
        } else {
            return new GitHubException(ExaError.messageBuilder("E-RD-GH-14") //
                    .message("{{originalMessage}}", originalMessage).toString(), exception);
        }
    }

    // [impl->dsn~retrieve-github-release-header-from-release-letter~2]
    // [impl->dsn~retrieve-github-release-body-from-release-letter~1]
    // [impl->dsn~upload-github-release-assets~1]
    // [impl->dsn~users-add-upload-definition-files-for-their-deliverables~1]
    @Override
    public GitHubReleaseInfo createGithubRelease(final GitHubRelease gitHubRelease, final Progress progress)
            throws GitHubException {
        try {
            final String repoName = gitHubRelease.getRepositoryName();
            final GHRepository repository = getRepository(repoName);
            final String version = gitHubRelease.getVersion();
            final GHRelease ghRelease = repository //
                    .createRelease(version) //
                    .draft(true) //
                    .body(gitHubRelease.getReleaseLetter()) //
                    .name(gitHubRelease.getHeader()) //
                    .create();
            if (gitHubRelease.hasUploadAssets()) {
                uploadAssets(repository, ghRelease.getUploadUrl(), progress);
            }
            for (final String tag : gitHubRelease.additionalTags()) {
                createTag(repository, version, tag);
            }
            return GitHubReleaseInfo.builder() //
                    .repositoryName(repoName) //
                    .version(version) //
                    .draft(ghRelease.isDraft()) //
                    .htmlUrl(ghRelease.getHtmlUrl()) //
                    .build();
        } catch (final IOException exception) {
            throw new GitHubException(
                    ExaError.messageBuilder("F-RD-GH-11")
                            .message("Exception happened during releasing a new tag on the GitHub.").toString(),
                    exception);
        }
    }

    private void uploadAssets(final GHRepository repository, final String uploadUrl, final Progress progress)
            throws GitHubException {
        final WorkflowOptions options = new WorkflowOptions() //
                .withProgress(progress) //
                .withDispatches(Map.of("upload_url", uploadUrl));
        try {
            executeWorkflow(repository, repository.getWorkflow(GITHUB_UPLOAD_ASSETS_WORKFLOW), options);
        } catch (final IOException exception) {
            throw new GitHubException(exception);
        }
    }

    // [impl->dsn~creating-git-tags~1]
    private void createTag(final GHRepository repository, final String tag, final String alias) throws GitHubException {
        try {
            final String sha = repository.getRef("refs/tags/" + tag).getObject().getSha();
            repository.createRef("refs/tags/" + alias, sha);
        } catch (final IOException exception) {
            // in case the alias already exists the API will throw an HttpException with message "Reference already
            // exists".
            throw new GitHubException(ExaError.messageBuilder("E-RD-GH-30") //
                    .message("Failed creating alias for tag {{tag}}.", tag).toString(), exception);
        }
    }

    @Override
    public Set<Integer> getClosedTickets(final String repositoryName) throws GitHubException {
        try {
            final List<GHIssue> closedIssues = this.getRepository(repositoryName).getIssues(GHIssueState.CLOSED);
            return closedIssues.stream().filter(ghIssue -> !ghIssue.isPullRequest()).map(GHIssue::getNumber)
                    .collect(Collectors.toSet());
        } catch (final IOException exception) {
            throw new GitHubException(
                    ExaError.messageBuilder("F-RD-GH-12")
                            .message("Unable to retrieve a list of closed tickets on the GitHub.").toString(),
                    exception);
        }
    }

    @Override
    public String getLatestTag(final String repositoryName) throws GitHubException {
        try {
            final GHRelease release = this.getRepository(repositoryName).getLatestRelease();
            return (release == null) ? null : release.getTagName();
        } catch (final IOException exception) {
            throw new RepositoryException(ExaError.messageBuilder("F-RD-GH-10")
                    .message("GitHub connection problem happened during retrieving the latest release.").toString(),
                    exception);
        }
    }

    @Override
    public String executeWorkflowWithLogs(final String repositoryName, final String workflowName,
            final WorkflowOptions options) throws GitHubException {
        try {
            final GHRepository repository = getRepository(repositoryName);
            final GHWorkflow workflow = repository.getWorkflow(workflowName);
            executeWorkflow(repository, workflow, options);
            return latestRun(workflow).downloadLogs(this::getStringFromInputStream);
        } catch (final IOException exception) {
            throw new GitHubException(exception);
        }
    }

    @Override
    public void executeWorkflow(final String repositoryName, final String workflowName, final WorkflowOptions options)
            throws GitHubException {
        try {
            final GHRepository repository = getRepository(repositoryName);
            executeWorkflow(repository, getRepository(repositoryName).getWorkflow(workflowName), options);
        } catch (final IOException exception) {
            throw new GitHubException(exception);
        }
    }

    private void executeWorkflow(final GHRepository repository, final GHWorkflow workflow,
            final WorkflowOptions options) throws GitHubException {
        try {
            workflow.dispatch(getDefaultBranch(repository), options.dispatches());
            LOGGER.info(() -> "Started GitHub workflow '" + workflow.getName() + "'");
            validateWorkflowConclusion(getWorkflowConclusion(workflow, options));
        } catch (final IOException exception) {
            throw new GitHubException(exception);
        }
    }

    // [impl->dsn~estimate-duration~1]
    // [impl->dsn~missing-estimation~1]
    @Override
    public Estimation estimateDuration(final String repositoryName, final String workflowName) {
        try {
            final GHWorkflow workflow = getRepository(repositoryName).getWorkflow(workflowName);
            final GHWorkflowRun run = latestRun(workflow);
            return run == null //
                    ? Estimation.empty()
                    : Estimation.from(run.getCreatedAt(), run.getUpdatedAt());
        } catch (IOException | GitHubException exception) {
            LOGGER.warning(ExaError.messageBuilder("W-RD-GH-29")
                    .message("Failed to retrieve duration of latest run of workflow {{workflow}}: {{cause|uq}}.", //
                            workflowName, exception) //
                    .mitigation("Executing workflow without estimation.") //
                    .toString());
            return Estimation.empty();
        }
    }

    /*
     * The fastest release takes 1-2 minutes, the slowest 1 hour and more. We send a request every 15 seconds hoping to
     * not exceed the GitHub request limits.
     */
    // [impl->dsn~progress-display~1]
    private String getWorkflowConclusion(final GHWorkflow workflow, final WorkflowOptions options)
            throws GitHubException, IOException {
        boolean reportUrl = true;
        final Duration timeout = Duration.ofMinutes(150);
        final Timer timer = new Timer() //
                .withTimeout(timeout) //
                .withSnoozeInterval(Duration.ofSeconds(15)) //
                .start();
        while (!timer.timeout()) {
            options.progress().reportStatus();
            waitSeconds(1);
            if (timer.alarm()) {
                timer.snooze();
                final GHWorkflowRun run = latestRun(workflow);
                if (reportUrl) {
                    reportUrl = false;
                    options.progress().hideStatus();
                    final String message = "URL: " + formatLink(run.getHtmlUrl());
                    LOGGER.info(() -> message);
                }
                if (run.getConclusion() != null) {
                    options.progress().newline();
                    return run.getConclusion().toString();
                }
            }
        }
        throw new GitHubException(getTimeoutExceptionMessage(timeout));
    }

    public GHWorkflowRun latestRun(final GHWorkflow workflow) {
        final PagedIterator<GHWorkflowRun> it = workflow.listRuns().iterator();
        return it.hasNext() ? it.next() : null;
    }

    private String getTimeoutExceptionMessage(final Duration timeout) {
        return ExaError.messageBuilder("E-RD-GH-3")
                .message("GitHub workflow runs too long. The timeout for monitoring is {{timeout|uq}}.")
                .parameter("timeout", Progress.formatDuration(timeout)) //
                .toString();
    }

    private void waitSeconds(final int seconds) {
        try {
            Thread.sleep(1000L * seconds);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private void validateWorkflowConclusion(final String workflowConclusion) throws GitHubException {
        if (!workflowConclusion.equalsIgnoreCase("success")) {
            throw new GitHubException(ExaError.messageBuilder("E-RD-GH-2")
                    .message("Workflow run failed. Run result: {{workflowConclusion}}")
                    .parameter("workflowConclusion", workflowConclusion)
                    .mitigation("Please check the action logs on the GitHub to analyze the problem.").toString());
        }
    }

    @Override
    public String getDefaultBranch(final String repositoryName) throws GitHubException {
        return getDefaultBranch(getRepository(repositoryName));
    }

    private String getDefaultBranch(final GHRepository repository) {
        return repository.getDefaultBranch();
    }

    @Override
    public InputStream getFileContent(final String repositoryName, final String branchName, final String filePath)
            throws GitHubException {
        try {
            final GHRepository repository = getRepository(repositoryName);
            final GHContent content = repository.getFileContent(filePath, branchName);
            return content.read();
        } catch (final IOException exception) {
            throw new GitHubException(ExaError.messageBuilder("F-RD-GH-7")
                    .message("Cannot find or read the file {{filePath}} in the repository {{repositoryName}}.")
                    .parameter("filePath", filePath) //
                    .parameter("repositoryName", repositoryName) //
                    .mitigation("Please add this file according to the user guide.").toString(), exception);
        }
    }

    @Override
    public void updateFileContent(final String repositoryName, final String branchName, final String filePath,
            final String newContent, final String commitMessage) throws GitHubException {
        try {
            final GHRepository repository = getRepository(repositoryName);
            final GHContent content = repository.getFileContent(filePath, branchName);
            content.update(newContent, commitMessage, branchName);
        } catch (final IOException exception) {
            throw new GitHubException(ExaError.messageBuilder("F-RD-GH-8")
                    .message("Cannot update the file {{filePath}} in the repository {{repositoryName}}.")
                    .parameter("filePath", filePath) //
                    .parameter("repositoryName", repositoryName).toString(), exception);
        }
    }

    @Override
    public String getRepositoryPrimaryLanguage(final String repositoryName) throws GitHubException {
        final GHRepository repository = getRepository(repositoryName);
        return repository.getLanguage();
    }

    @Override
    public List<Long> getRepositoryArtifactsIds(final String repositoryName) throws GitHubException {
        final PagedIterable<GHArtifact> artifacts = getRepository(repositoryName).listArtifacts();
        return collectAliveArtifactsIds(artifacts);
    }

    private List<Long> collectAliveArtifactsIds(final PagedIterable<GHArtifact> artifacts) {
        final List<Long> gitHubArtifacts = new ArrayList<>();
        for (final GHArtifact artifact : artifacts) {
            if (!artifact.isExpired()) {
                gitHubArtifacts.add(artifact.getId());
            }
        }
        return gitHubArtifacts;
    }

    @Override
    public String downloadArtifactAsString(final String repositoryName, final long artifactId) throws GitHubException {
        try {
            final GHArtifact artifact = getRepository(repositoryName).getArtifact(artifactId);
            return artifact.download(this::getStringFromInputStream);
        } catch (final IOException exception) {
            throw new GitHubException(exception);
        }
    }

    private String getStringFromInputStream(final InputStream input) throws IOException {
        try (final var byteArrayOutputStream = new ByteArrayOutputStream();
                final var zipInputStream = new ZipInputStream(input)) {
            while (zipInputStream.getNextEntry() != null) {
                byteArrayOutputStream.write(zipInputStream.readAllBytes());
            }
            return byteArrayOutputStream.toString();
        }
    }

    @Override
    public void deleteAllArtifacts(final String repositoryName) throws GitHubException {
        final PagedIterable<GHArtifact> artifacts = getRepository(repositoryName).listArtifacts();
        for (final GHArtifact artifact : artifacts) {
            try {
                artifact.delete();
            } catch (final IOException exception) {
                throw new GitHubException(exception);
            }
        }
    }
}