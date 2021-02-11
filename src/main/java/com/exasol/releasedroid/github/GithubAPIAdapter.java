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
import com.exasol.releasedroid.repository.RepositoryException;

/**
 * Implements an adapter to interact with Github.
 */
public class GithubAPIAdapter implements GithubGateway {
    private static final Logger LOGGER = Logger.getLogger(GithubAPIAdapter.class.getName());
    private static final String GITHUB_API_ENTRY_URL = "https://api.github.com/repos/";
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
    private void uploadAssets(final String repositoryFullName, final String uploadUrl, final String defaultBranchName)
            throws GitHubException {
        final JSONObject body = new JSONObject();
        body.put("ref", defaultBranchName);
        final JSONObject inputs = new JSONObject();
        inputs.put("upload_url", uploadUrl);
        body.put("inputs", inputs);
        final String json = body.toString();
        executeWorkflow(repositoryFullName, "github_release.yml", json);
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
        final HttpResponse<String> response = sendGitHubPostRequest(uri, payload);
        validateResponse(response);
        logMessage(workflowName);
        final String workflowConclusion = getWorkflowConclusion(workflowUriPrefix);
        validateWorkflowConclusion(workflowConclusion);
    }

    private void logMessage(final String workflowName) {
        LOGGER.info(() -> "A GitHub workflow '" + workflowName
                + "' has started. The Release Droid is monitoring its progress. "
                + "This can take from a few minutes to a couple of hours depending on the build.");
    }

    private HttpResponse<String> sendGitHubPostRequest(final URI uri, final String body) throws GitHubException {
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(uri) //
                .header("Accept", "application/vnd.github.v3+json") //
                .header("Authorization", "token " + this.gitHubUser.getToken()) //
                .header("Content-Type", "application/json") //
                .POST(HttpRequest.BodyPublishers.ofString(body)) //
                .build();
        return sendGitHubRequest(request);
    }

    private HttpResponse<String> sendGitHubRequest(final HttpRequest request) throws GitHubException {
        final HttpClient build = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
        try {
            return build.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (final IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitHubException(
                    ExaError.messageBuilder("F-RR-GH-2")
                            .message("Exception happened during sending an HTTP request to the GitHub.").toString(),
                    exception);
        }
    }

    private void validateResponse(final HttpResponse<String> response) throws GitHubException {
        if ((response.statusCode() < HttpURLConnection.HTTP_OK)
                || (response.statusCode() >= HttpURLConnection.HTTP_MULT_CHOICE)) {
            throw new GitHubException(ExaError.messageBuilder("F-RR-GH-6")
                    .message("An executing workflow HTTP request failed. Cause: {{cause}}")
                    .unquotedParameter("cause", response.body()).toString());
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
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(uri) //
                .header("Accept", "application/vnd.github.v3+json") //
                .header("Authorization", "token " + this.gitHubUser.getToken()) //
                .header("Content-Type", "application/json") //
                .GET() //
                .build();
        return sendGitHubRequest(request);
    }

    private HttpResponse<String> sendGitHubDeleteRequest(final URI uri) throws GitHubException {
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(uri) //
                .header("Accept", "application/vnd.github.v3+json") //
                .header("Authorization", "token " + this.gitHubUser.getToken()) //
                .header("Content-Type", "application/json") //
                .DELETE() //
                .build();
        return sendGitHubRequest(request);
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
    public List<GitHubArtifact> getRepositoryArtifacts(final String repositoryName) throws GitHubException {
        final URI uri = URI.create(GITHUB_API_ENTRY_URL + repositoryName + "/actions/artifacts");
        final List<GitHubArtifact> gitHubArtifacts = new ArrayList<>();
        final HttpResponse<String> response = sendGitHubGetRequest(uri);
        final JSONObject jsonObject = new JSONObject(response.body());
        final JSONArray artifacts = jsonObject.getJSONArray("artifacts");
        for (int i = 0; i < artifacts.length(); i++) {
            final JSONObject artifact = artifacts.getJSONObject(i);
            if (!artifact.getBoolean("expired")) {
                gitHubArtifacts.add(GitHubArtifact.builder() //
                        .id(String.valueOf(artifact.getLong("id"))) //
                        .name(artifact.getString("name")) //
                        .archiveDownloadUrl(artifact.getString("archive_download_url")) //
                        .build());
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
        executeWorkflow(repositoryName, "prepare_origin_checksum.yml", json);
    }

    @Override
    public Map<String, String> downloadChecksumFromArtifactory(final String artifactId) throws GitHubException {
        final HttpResponse<String> stringHttpResponse = sendGitHubGetRequest(URI.create(
                "https://api.github.com/repos/exasol/testing-release-robot/actions/artifacts/" + artifactId + "/zip"));
        final String location = stringHttpResponse.headers().firstValue("location").orElseThrow();
        final URL url;
        try {
            url = new URL(location);
        } catch (final MalformedURLException e) {
            throw new GitHubException("");
        }
        try (final BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
                final ZipInputStream zis = new ZipInputStream(inputStream);
                final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            int read = 0;
            while (zis.getNextEntry() != null) {
                while ((read = zis.read(buffer, 0, buffer.length)) > 0) {
                    baos.write(buffer, 0, read);
                }
            }
            return createMap(baos.toString().replace("\n", " ").strip());
        } catch (final IOException exception) {
            throw new GitHubException("", exception);
        }
    }

    private Map<String, String> createMap(final String hashsum) {
        final Map<String, String> map = new HashMap<>();
        final List<String> s = new ArrayList<>();
        String cur = "";
        for (final char ch : hashsum.toCharArray()) {
            if (ch == ' ' || ch == '\n' || ch == '\r') {
                if (!cur.isEmpty()) {
                    s.add(cur);
                }
                cur = "";
            } else {
                cur += ch;
            }
        }
        if (!cur.isEmpty()) {
            s.add(cur);
        }

        for (int i = 0; i < s.size() - 1; i += 2) {
            map.put(s.get(i + 1), s.get(i));
        }
        return map;
    }

    @Override
    public Map<String, String> createQuickCheckSum(final String repositoryName) throws GitHubException {
        final GHRepository repository = getRepository(repositoryName);
        final JSONObject body = new JSONObject();
        body.put("ref", repository.getDefaultBranch());
        final String json = body.toString();
        executeWorkflow(repositoryName, "print_quick_checksum.yml", json);

        final String workflowUriPrefix = GITHUB_API_ENTRY_URL + "exasol/testing-release-robot"
                + "/actions/workflows/print_quick_checksum.yml/runs";
        final URI uri = URI.create(workflowUriPrefix);
        final HttpResponse<String> response = sendGitHubGetRequest(uri);
        final String logsUrl = new JSONObject(response.body()).getJSONArray("workflow_runs").getJSONObject(0)
                .getString("logs_url");
        final HttpResponse<String> stringHttpResponse = sendGitHubGetRequest(URI.create(logsUrl));
        final String location = stringHttpResponse.headers().firstValue("location").orElseThrow();
        final URL url;
        try {
            url = new URL(location);
        } catch (final MalformedURLException e) {
            throw new GitHubException("");
        }
        try (final BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
                final ZipInputStream zis = new ZipInputStream(inputStream);
                final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            int read = 0;
            while (zis.getNextEntry() != null) {
                while ((read = zis.read(buffer, 0, buffer.length)) > 0) {
                    baos.write(buffer, 0, read);
                }
            }
            final String logs = baos.toString();

            final String[] splitOnSpace = logs
                    .substring(logs.lastIndexOf("checksum_start=="), logs.lastIndexOf("==checksum_end"))
                    .replace("\n", " ").split(" ");
            return createMap(String.join(" ", Arrays.asList(splitOnSpace).subList(2, splitOnSpace.length - 1)));
        } catch (final IOException exception) {
            throw new GitHubException("", exception);
        }
    }

    @Override
    public void deleteAllArtifactsOnRepository(final String repositoryName) throws GitHubException {
        final List<GitHubArtifact> repositoryArtifacts = getRepositoryArtifacts(repositoryName);
        final String uriPrefix = GITHUB_API_ENTRY_URL + "exasol/testing-release-robot" + "/actions/artifacts/";
        for (final GitHubArtifact repositoryArtifact : repositoryArtifacts) {
            sendGitHubDeleteRequest(URI.create(uriPrefix + repositoryArtifact.getId()));
        }
    }
}