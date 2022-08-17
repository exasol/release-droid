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
import com.exasol.releasedroid.adapter.github.progress.ProgressFormatter;
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

    @Override
    // [impl->dsn~retrieve-github-release-header-from-release-letter~2]
    // [impl->dsn~retrieve-github-release-body-from-release-letter~1]
    public GitHubReleaseInfo createGithubRelease(final GitHubRelease gitHubRelease) throws GitHubException {
        try {
            final GHRelease ghRelease = this.getRepository(gitHubRelease.getRepositoryName())//
                    .createRelease(gitHubRelease.getVersion()) //
                    .draft(true) //
                    .body(gitHubRelease.getReleaseLetter()) //
                    .name(gitHubRelease.getHeader()) //
                    .create();
            if (gitHubRelease.hasUploadAssets()) {
                final String uploadUrl = ghRelease.getUploadUrl();
                executeWorkflowToUploadAssets(gitHubRelease.getRepositoryName(), uploadUrl);
            }
            return GitHubReleaseInfo.builder() //
                    .repositoryName(gitHubRelease.getRepositoryName()) //
                    .version(gitHubRelease.getVersion()) //
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

    // [impl->dsn~upload-github-release-assets~1]
    // [impl->dsn~users-add-upload-definition-files-for-their-deliverables~1]
    private void executeWorkflowToUploadAssets(final String repositoryName, final String uploadUrl)
            throws GitHubException {
        executeWorkflow(repositoryName, GITHUB_UPLOAD_ASSETS_WORKFLOW, Map.of("upload_url", uploadUrl));
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
    public void executeWorkflow(final String repositoryName, final String workflowName,
            final Map<String, Object> dispatches) throws GitHubException {
        try {
            final GHRepository repository = getRepository(repositoryName);
            final GHWorkflow workflow = repository.getWorkflow(workflowName);
            final ProgressFormatter.Builder builder = progressFormatterBuilder(workflow) //
                    .timeout(Duration.ofMinutes(150)) //
                    .callbackInterval(Duration.ofSeconds(15));
            workflow.dispatch(getDefaultBranch(repositoryName), dispatches);
            final ProgressFormatter progress = builder.start();
            final String prefix = progress.startTime() //
                    + ": Started GitHub workflow '" + workflowName + "'.\n";
            LOGGER.info(() -> progress.welcomeMessage(prefix));
            validateWorkflowConclusion(getWorkflowConclusion(progress, workflow));
        } catch (final IOException exception) {
            throw new GitHubException(exception);
        }
    }

    private ProgressFormatter.Builder progressFormatterBuilder(final GHWorkflow workflow) throws IOException {
        final ProgressFormatter.Builder builder = ProgressFormatter.builder();
        final GHWorkflowRun lastRun = latestRun(workflow);
        return lastRun == null //
                ? builder //
                : builder.lastRun(lastRun.getCreatedAt(), lastRun.getUpdatedAt());
    }

    @Override
    public void executeWorkflow(final String repositoryName, final String workflowName) throws GitHubException {
        executeWorkflow(repositoryName, workflowName, Collections.emptyMap());
    }

    @Override
    public String executeWorkflowWithLogs(final String repositoryName, final String workflowName)
            throws GitHubException {
        executeWorkflow(repositoryName, workflowName);
        final GHRepository repository = getRepository(repositoryName);
        try {
            final long workflowId = repository.getWorkflow(workflowName).getId();
            final long workflowRunId = findLastWorkflowRunId(repository, workflowId);
            return repository.getWorkflowRun(workflowRunId).downloadLogs(this::getStringFromInputStream);
        } catch (final IOException exception) {
            throw new GitHubException(exception);
        }
    }

    /*
     * The fastest release takes 1-2 minutes, the slowest 1 hour and more. We send a request every 15 seconds hoping to
     * not exceed the GitHub request limits.
     */
    // suppressing warnings for java:S106 - Standard outputs should not be used directly to log anything
    // since GitHubAPIAdapter is intended to print on standard out.
    // Using a logger cannot overwrite the current line.
    @SuppressWarnings("java:S106")
    private String getWorkflowConclusion(final ProgressFormatter progress, final GHWorkflow workflow)
            throws GitHubException, IOException {
        boolean reportUrl = true;
        while (!progress.timeout()) {
            System.out.print("\r" + progress.status());
            System.out.flush();
            waitSeconds(1);
            if (progress.getMonitor().needsCallback()) {
                progress.getMonitor().notifyCallback();
                final GHWorkflowRun run = latestRun(workflow);
                if (reportUrl) {
                    reportUrl = false;
                    System.out.print("\r");
                    LOGGER.info("URL: " + formatLink(run.getHtmlUrl()));
                }
                if (run.getConclusion() != null) {
                    System.out.println();
                    return run.getConclusion().toString();
                }
            }
        }
        throw new GitHubException(getTimeoutExceptionMessage(progress.formatElapsed()));
    }

    public GHWorkflowRun latestRun(final GHWorkflow workflow) {
        final PagedIterator<GHWorkflowRun> it = workflow.listRuns().iterator();
        return it.hasNext() ? it.next() : null;
    }

    private String getTimeoutExceptionMessage(final String elapsed) {
        return ExaError.messageBuilder("E-RD-GH-3")
                .message("GitHub workflow runs too long. The timeout for monitoring is {{timeout}} hours.")
                .parameter("timeout", elapsed) //
                .toString();
    }

    private long findLastWorkflowRunId(final GHRepository repository, final long workflowId)
            throws IOException, GitHubException {
        GHWorkflowRun lastRun = null;
        for (final GHWorkflowRun ghWorkflowRun : repository.queryWorkflowRuns().list()) {
            if ((ghWorkflowRun.getWorkflowId() == workflowId)
                    && ((lastRun == null) || ghWorkflowRun.getCreatedAt().after(lastRun.getCreatedAt()))) {
                lastRun = ghWorkflowRun;
            }
        }
        validateLastRun(workflowId, lastRun);
        return lastRun.getId();
    }

    private void validateLastRun(final long workflowId, final GHWorkflowRun lastRun) throws GitHubException {
        if (lastRun == null) {
            throw new GitHubException(ExaError.messageBuilder("E-RD-GH-4") //
                    .message("Cannot find runs of GitHub workflow with id {{workflowId}}.") //
                    .parameter("workflowId", workflowId) //
                    .toString());
        }
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
        return getRepository(repositoryName).getDefaultBranch();
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