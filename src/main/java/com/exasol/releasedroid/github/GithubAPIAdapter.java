package com.exasol.releasedroid.github;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kohsuke.github.*;

import com.exasol.errorreporting.ErrorMessageBuilder;
import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.formatting.ChecksumFormatter;
import com.exasol.releasedroid.repository.RepositoryException;

/**
 * Implements an adapter to interact with Github.
 */
public class GithubAPIAdapter implements GithubGateway {
    private static final Logger LOGGER = Logger.getLogger(GithubAPIAdapter.class.getName());
    private static final String GITHUB_API_ENTRY_URL = "https://api.github.com/repos/";
    private static final String PREPARE_ORIGINAL_CHECKSUM_WORKFLOW = "release_droid_prepare_original_checksum.yml";
    private static final String GITHUB_RELEASE_WORKFLOW = "release_droid_upload_github_release_assets.yml";
    private static final String PRINT_QUICK_CHECKSUM_WORKFLOW = "release_droid_print_quick_checksum.yml";
    private final Map<String, GHRepository> repositories;
    private final GitHubUser gitHubUser;

    /**
     * Create a new instance of {@link GithubAPIAdapter}.
     *
     * @param gitHubUser instance of {@link GitHubUser}
     */
    public GithubAPIAdapter(final GitHubUser gitHubUser) {
        this.gitHubUser = gitHubUser;
        this.repositories = new HashMap<>();
    }

    private GHRepository createGHRepository(final String repositoryName, final GitHubUser user) throws GitHubException {
        try {
            final GitHub gitHub = GitHub.connect(user.getUsername(), user.getToken());
            return gitHub.getRepository(repositoryName);
        } catch (final IOException exception) {
            throw wrapGitHubException(repositoryName, exception);
        }
    }

    private GitHubException wrapGitHubException(final String repositoryName, final IOException exception) {
        final String originalMessage = exception.getMessage();
        final ErrorMessageBuilder errorMessageBuilder = ExaError.messageBuilder("E-RR-GH-1");
        if (originalMessage.contains("Not Found")) {
            errorMessageBuilder.message(
                    "Repository {{input}} not found. The repository doesn't exist or the user doesn't have permissions to see it.")
                    .parameter("repositoryName", repositoryName);
        } else if (originalMessage.contains("Bad credentials")) {
            errorMessageBuilder.message("A GitHub account with specified username and password doesn't exist.");
        } else {
            errorMessageBuilder.message(originalMessage);
        }
        return new GitHubException(errorMessageBuilder.toString(), exception);
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
            final String uploadUrl = ghRelease.getUploadUrl();
            uploadAssets(gitHubRelease.getRepositoryName(), uploadUrl, gitHubRelease.getDefaultBranchName());
        } catch (final IOException exception) {
            throw new GitHubException(
                    ExaError.messageBuilder("F-RR-GH-3")
                            .message("Exception happened during releasing a new tag on the GitHub.").toString(),
                    exception);
        }
    }

    private GHRepository getRepository(final String repositoryName) throws GitHubException {
        if (!this.repositories.containsKey(repositoryName)) {
            this.repositories.put(repositoryName, this.createGHRepository(repositoryName, this.gitHubUser));
        }
        return this.repositories.get(repositoryName);
    }

    // [impl->dsn~upload-github-release-assets~1]
    // [impl->dsn~users-add-upload-definition-files-for-their-deliverables~1]
    private void uploadAssets(final String repositoryFullName, final String uploadUrl, final String defaultBranchName)
            throws GitHubException {
        final JSONObject body = new JSONObject();
        body.put("ref", defaultBranchName);
        final JSONObject inputs = new JSONObject();
        inputs.put("upload_url", uploadUrl);
        body.put("inputs", inputs);
        final String json = body.toString();
        executeWorkflow(repositoryFullName, GITHUB_RELEASE_WORKFLOW, json);
    }

    @Override
    public Set<Integer> getClosedTickets(final String repositoryName) throws GitHubException {
        try {
            final List<GHIssue> closedIssues = this.getRepository(repositoryName).getIssues(GHIssueState.CLOSED);
            return closedIssues.stream().filter(ghIssue -> !ghIssue.isPullRequest()).map(GHIssue::getNumber)
                    .collect(Collectors.toSet());
        } catch (final IOException exception) {
            throw new GitHubException(
                    ExaError.messageBuilder("F-RR-GH-4")
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
            throw new RepositoryException(ExaError.messageBuilder("F-RR-GH-5")
                    .message("GitHub connection problem happened during retrieving the latest release.").toString(),
                    exception);
        }
    }

    @Override
    public void executeWorkflow(final String repositoryName, final String workflowName, final String payload)
            throws GitHubException {
        final String workflowUriPrefix = GITHUB_API_ENTRY_URL + repositoryName + "/actions/workflows/" + workflowName;
        final URI uri = URI.create(workflowUriPrefix + "/dispatches");
        sendGitHubPostRequest(uri, payload);
        logMessage(workflowName);
        final String workflowConclusion = getWorkflowConclusion(workflowUriPrefix);
        validateWorkflowConclusion(workflowConclusion);
    }

    private void logMessage(final String workflowName) {
        LOGGER.info(() -> "A GitHub workflow '" + workflowName
                + "' has started. The Release Droid is monitoring its progress. "
                + "This can take from a few minutes to a couple of hours depending on the build.");
    }

    private void sendGitHubPostRequest(final URI uri, final String body) throws GitHubException {
        final HttpRequest request = getGitHubHttpRequestBuilder() //
                .uri(uri) //
                .POST(HttpRequest.BodyPublishers.ofString(body)) //
                .build();
        sendGitHubRequest(request);
    }

    private HttpRequest.Builder getGitHubHttpRequestBuilder() {
        return HttpRequest.newBuilder() //
                .header("Accept", "application/vnd.github.v3+json") //
                .header("Authorization", "token " + this.gitHubUser.getToken()) //
                .header("Content-Type", "application/json");
    }

    private HttpResponse<String> sendGitHubRequest(final HttpRequest request) throws GitHubException {
        final HttpClient build = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
        try {
            final HttpResponse<String> response = build.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response);
            return response;
        } catch (final IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubException(
                    ExaError.messageBuilder("F-RR-GH-2")
                            .message("Exception happened during sending an HTTP request to the GitHub.").toString(),
                    exception);
        }
    }

    private void validateResponse(final HttpResponse<String> response) throws GitHubException {
        if ((response.statusCode() < HttpURLConnection.HTTP_OK) || (response.statusCode() > 302)) {
            throw new GitHubException(ExaError.messageBuilder("F-RR-GH-6")
                    .message("An HTTP request to GitHub returned a bad response. Cause: {{cause}}")
                    .unquotedParameter("cause", response.statusCode() + " " + response.body()).toString());
        }
    }

    private String getWorkflowConclusion(final String workflowUriPrefix) throws GitHubException {
        int minutesPassed = 0;
        while (true) {
            final int minutes = getNextResultCheckDelayInMinutes(minutesPassed);
            minutesPassed += minutes;
            waitMinutes(minutes);
            final URI uri = URI.create(workflowUriPrefix + "/runs");
            final HttpResponse<String> response = sendGitHubGetRequest(uri);
            final JSONObject lastRun = new JSONObject(response.body()).getJSONArray("workflow_runs").getJSONObject(0);
            final boolean actionCompleted = !lastRun.isNull("conclusion");
            if (actionCompleted) {
                return lastRun.getString("conclusion");
            }
        }
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

    private HttpResponse<String> sendGitHubGetRequest(final URI uri) throws GitHubException {
        final HttpRequest request = getGitHubHttpRequestBuilder() //
                .uri(uri) //
                .GET() //
                .build();
        return sendGitHubRequest(request);
    }

    private void sendGitHubDeleteRequest(final URI uri) throws GitHubException {
        final HttpRequest request = getGitHubHttpRequestBuilder() //
                .uri(uri) //
                .DELETE() //
                .build();
        sendGitHubRequest(request);
    }

    private void validateWorkflowConclusion(final String workflowConclusion) throws GitHubException {
        if (!workflowConclusion.equals("success")) {
            throw new GitHubException(ExaError.messageBuilder("E-RR-GH-2")
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
            throw new GitHubException(ExaError.messageBuilder("F-RR-GH-7")
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
            throw new GitHubException(ExaError.messageBuilder("F-RR-GH-8")
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
    public List<String> getRepositoryArtifactsIds(final String repositoryName) throws GitHubException {
        final URI uri = URI.create(GITHUB_API_ENTRY_URL + repositoryName + "/actions/artifacts");
        final HttpResponse<String> response = sendGitHubGetRequest(uri);
        final JSONArray artifacts = new JSONObject(response.body()).getJSONArray("artifacts");
        return collectAliveArtifactsIds(artifacts);
    }

    private List<String> collectAliveArtifactsIds(final JSONArray artifacts) {
        final List<String> gitHubArtifacts = new ArrayList<>();
        for (int i = 0; i < artifacts.length(); i++) {
            final JSONObject artifact = artifacts.getJSONObject(i);
            if (!artifact.getBoolean("expired")) {
                gitHubArtifacts.add(String.valueOf(artifact.getLong("id")));
            }
        }
        return gitHubArtifacts;
    }

    @Override
    public void createChecksumArtifact(final String repositoryName) throws GitHubException {
        final GHRepository repository = getRepository(repositoryName);
        final JSONObject body = new JSONObject();
        body.put("ref", repository.getDefaultBranch());
        final String json = body.toString();
        executeWorkflow(repositoryName, PREPARE_ORIGINAL_CHECKSUM_WORKFLOW, json);
    }

    @Override
    public Map<String, String> downloadChecksumFromArtifactory(final String repositoryName, final String artifactId)
            throws GitHubException {
        final URI uri = URI.create(GITHUB_API_ENTRY_URL + repositoryName + "/actions/artifacts/" + artifactId + "/zip");
        final HttpResponse<String> stringHttpResponse = sendGitHubGetRequest(uri);
        final String downloadLink = stringHttpResponse.headers().firstValue("location").orElseThrow();
        final URI checksumDownloadURI = URI.create(downloadLink);
        return ChecksumFormatter.createChecksumMap(downloadZippedFileAsString(checksumDownloadURI));
    }

    private String downloadZippedFileAsString(final URI downloadURI) throws GitHubException {
        try (final BufferedInputStream bufferedInputStream = new BufferedInputStream(downloadURI.toURL().openStream());
                final ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            int read = 0;
            while (zipInputStream.getNextEntry() != null) {
                while ((read = zipInputStream.read(buffer, 0, buffer.length)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, read);
                }
            }
            return byteArrayOutputStream.toString();
        } catch (final IOException exception) {
            throw new GitHubException(ExaError.messageBuilder("F-RR-GH-1") //
                    .message("Cannot download a file from artifactory.").toString(), exception);
        }
    }

    @Override
    public Map<String, String> createQuickCheckSum(final String repositoryName) throws GitHubException {
        final GHRepository repository = getRepository(repositoryName);
        executeQuickCheckSumWorkflow(repositoryName, repository.getDefaultBranch());
        final URI logsDownloadURI = getLogsDownloadURI(repositoryName);
        final String logs = downloadZippedFileAsString(logsDownloadURI);
        return formatChecksumLogs(logs);

    }

    private Map<String, String> formatChecksumLogs(final String logs) {
        final String[] splittedLogs = logs
                .substring(logs.lastIndexOf("checksum_start=="), logs.lastIndexOf("==checksum_end")).replace("\n", " ")
                .split(" ");
        return ChecksumFormatter
                .createChecksumMap(String.join(" ", Arrays.asList(splittedLogs).subList(2, splittedLogs.length - 1)));
    }

    private void executeQuickCheckSumWorkflow(final String repositoryName, final String defaultBranch)
            throws GitHubException {
        final JSONObject body = new JSONObject();
        body.put("ref", defaultBranch);
        final String json = body.toString();
        executeWorkflow(repositoryName, PRINT_QUICK_CHECKSUM_WORKFLOW, json);
    }

    private URI getLogsDownloadURI(final String repositoryName) throws GitHubException {
        final HttpResponse<String> workflowRuns = sendGitHubGetRequest(URI.create(GITHUB_API_ENTRY_URL + repositoryName
                + "/actions/workflows/" + PRINT_QUICK_CHECKSUM_WORKFLOW + "/runs"));
        final String logsUrl = new JSONObject(workflowRuns.body()).getJSONArray("workflow_runs").getJSONObject(0)
                .getString("logs_url");
        return URI.create(sendGitHubGetRequest(URI.create(logsUrl)).headers().firstValue("location").orElseThrow());
    }

    @Override
    public void deleteAllArtifacts(final String repositoryName) throws GitHubException {
        final List<String> artifacts = getRepositoryArtifactsIds(repositoryName);
        final String uriPrefix = GITHUB_API_ENTRY_URL + repositoryName + "/actions/artifacts/";
        for (final String artifactId : artifacts) {
            sendGitHubDeleteRequest(URI.create(uriPrefix + artifactId));
        }
    }
}