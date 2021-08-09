package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.github.GitHubConstants.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import org.kohsuke.github.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.formatting.ChecksumFormatter;
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
    // [impl->dsn~retrieve-github-release-header-from-release-letter~1]
    // [impl->dsn~retrieve-github-release-body-from-release-letter~1]
    public void createGithubRelease(final GitHubRelease gitHubRelease) throws GitHubException {
        try {
            final GHRelease ghRelease = this.getRepository(gitHubRelease.getRepositoryName())//
                    .createRelease(gitHubRelease.getVersion()) //
                    .draft(true) //
                    .body(gitHubRelease.getReleaseLetter()) //
                    .name(gitHubRelease.getHeader()) //
                    .create();
            if (gitHubRelease.uploadAssets()) {
                final String uploadUrl = ghRelease.getUploadUrl();
                executeWorkflowToUploadAssets(gitHubRelease.getRepositoryName(), uploadUrl);
            }
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
            workflow.dispatch(getDefaultBranch(repositoryName), dispatches);
            logMessage(workflowName);
            validateWorkflowConclusion(getWorkflowConclusion(repository, workflow.getId()));
        } catch (final IOException exception) {
            throw new GitHubException(exception);
        }
    }

    @Override
    public void executeWorkflow(final String repositoryName, final String workflowName) throws GitHubException {
        executeWorkflow(repositoryName, workflowName, Collections.emptyMap());
    }

    private void logMessage(final String workflowName) {
        LOGGER.info(() -> "A GitHub workflow '" + workflowName
                + "' has started. The Release Droid is monitoring its progress. "
                + "This can take from a few minutes to a couple of hours depending on the build.");
    }

    private String getWorkflowConclusion(final GHRepository repository, final long workflowId)
            throws GitHubException, IOException {
        final var workflowMonitoringTimeout = 150;
        int minutesPassed = 0;
        long lastWorkflowRunId = -1;
        while (minutesPassed < workflowMonitoringTimeout) {
            final int minutes = getNextResultCheckDelayInMinutes(minutesPassed);
            minutesPassed += minutes;
            waitMinutes(minutes);
            LOGGER.info(getMessage(minutesPassed));
            if (lastWorkflowRunId == -1) {
                lastWorkflowRunId = findLastWorkflowRunId(repository, workflowId);
            }
            final var ghWorkflowRun = getWorkflowRunById(repository, lastWorkflowRunId);
            final boolean actionCompleted = ghWorkflowRun.getConclusion() != null;
            if (actionCompleted) {
                return ghWorkflowRun.getConclusion().toString();
            }
        }
        throw new GitHubException(getTimeoutExceptionMessage(minutesPassed));
    }

    private String getMessage(final int minutesPassed) {
        return "Workflow is running for about " + minutesPassed + " minutes.";
    }

    private String getTimeoutExceptionMessage(final int minutesPassed) {
        return ExaError.messageBuilder("E-RD-GH-3")
                .message("GitHub workflow runs too long. The timeout for monitoring is {{timeout}} minutes.")
                .parameter("timeout", minutesPassed) //
                .toString();
    }

    private long findLastWorkflowRunId(final GHRepository repository, final long workflowId)
            throws IOException, GitHubException {
        GHWorkflowRun lastRun = null;
        for (final GHWorkflowRun ghWorkflowRun : repository.queryWorkflowRuns().list()) {
            if (ghWorkflowRun.getWorkflowId() == workflowId
                    && (lastRun == null || ghWorkflowRun.getCreatedAt().after(lastRun.getCreatedAt()))) {
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

    private GHWorkflowRun getWorkflowRunById(final GHRepository repository, final long lastWorkflowRunId)
            throws GitHubException {
        for (final GHWorkflowRun ghWorkflowRun : repository.queryWorkflowRuns().list()) {
            if (ghWorkflowRun.getId() == lastWorkflowRunId) {
                return ghWorkflowRun;
            }
        }
        throw new GitHubException(ExaError.messageBuilder("E-RD-GH-5") //
                .message("GitHub workflow run with id {{id}} not found") //
                .parameter("id", lastWorkflowRunId) //
                .toString());
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
    public void createChecksumArtifact(final String repositoryName) throws GitHubException {
        executeWorkflow(repositoryName, PREPARE_ORIGINAL_CHECKSUM_WORKFLOW);
    }

    @Override
    public Map<String, String> downloadChecksumFromArtifactory(final String repositoryName, final long artifactId)
            throws GitHubException {
        try {
            final GHArtifact artifact = getRepository(repositoryName).getArtifact(artifactId);
            final var checksumAsString = artifact.download(this::getStringFromInputStream);
            return ChecksumFormatter.createChecksumMap(checksumAsString);
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
    public Map<String, String> createQuickCheckSum(final String repositoryName) throws GitHubException {
        executeWorkflow(repositoryName, PRINT_QUICK_CHECKSUM_WORKFLOW);
        final GHRepository repository = getRepository(repositoryName);
        try {
            final long workflowId = repository.getWorkflow(PRINT_QUICK_CHECKSUM_WORKFLOW).getId();
            final long workflowRunId = findLastWorkflowRunId(repository, workflowId);
            final String logs = repository.getWorkflowRun(workflowRunId).downloadLogs(this::getStringFromInputStream);
            return formatChecksumLogs(logs);
        } catch (final IOException exception) {
            throw new GitHubException(exception);
        }
    }

    private Map<String, String> formatChecksumLogs(final String logs) {
        final String[] splittedLogs = logs
                .substring(logs.lastIndexOf("checksum_start=="), logs.lastIndexOf("==checksum_end")).replace("\n", " ")
                .split(" ");
        return ChecksumFormatter
                .createChecksumMap(String.join(" ", Arrays.asList(splittedLogs).subList(2, splittedLogs.length - 1)));
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