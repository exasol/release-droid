package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.io.*;
import java.util.Optional;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.BaseRepository;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;
import com.exasol.releasedroid.usecases.repository.version.Version;

/**
 * This class represents a GitHub-based repository.
 */
// [impl->dsn~github-repository~1]
public class GitHubRepositoryGate implements RepositoryGate {
    private final GitHubGateway githubGateway;
    private final String branchName;
    private final String fullName;

    /**
     * Create a new instance of {@link BaseRepository}.
     *
     * @param githubGateway an instance of {@link GitHubGateway}
     * @param branchName    name of a branch to get content from
     * @param fullName      fully qualified name of the repository
     */
    public GitHubRepositoryGate(final GitHubGateway githubGateway, final String branchName, final String fullName) {
        this.githubGateway = githubGateway;
        this.branchName = branchName;
        this.fullName = fullName;
    }

    @Override
    public String getSingleFileContentAsString(final String filePath) {
        try {
            return getContent(this.githubGateway.getFileContent(getName(), this.branchName, filePath));
        } catch (final IOException exception) {
            throw new RepositoryException(ExaError.messageBuilder("F-RD-GH-25")
                    .message("Cannot convert the file {{filePath}} in the repository {{repositoryName}} to a string.")
                    .parameter("filePath", filePath) //
                    .parameter("repositoryName", getName()).toString(), exception);
        } catch (final GitHubException exception) {
            throw new RepositoryException(exception);
        }
    }

    @Override
    public boolean hasFile(final String filePath) {
        try {
            this.githubGateway.getFileContent(getName(), this.branchName, filePath);
            return true;
        } catch (final GitHubException exception) {
            return false;
        }
    }

    private String getContent(final InputStream stream) throws IOException {
        final var result = new StringBuilder();
        try (final var reader = new BufferedReader(new InputStreamReader(stream))) {
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                result.append(LINE_SEPARATOR);
                line = reader.readLine();
            }
        }
        return result.toString().stripTrailing();
    }

    @Override
    public void updateFileContent(final String filePath, final String newContent, final String commitMessage) {
        try {
            this.githubGateway.updateFileContent(getName(), this.branchName, filePath, newContent, commitMessage);
        } catch (final GitHubException exception) {
            throw new RepositoryException(
                    ExaError.messageBuilder("E-RD-GH-27")
                            .message("Cannot update a file in the repository: {{file}}", filePath).toString(),
                    exception);
        }
    }

    @Override
    public boolean isOnDefaultBranch() {
        try {
            return this.githubGateway.getDefaultBranch(this.getName()).equals(this.branchName);
        } catch (final GitHubException exception) {
            throw new RepositoryException(exception);
        }
    }

    @Override
    public String getBranchName() {
        return this.branchName;
    }

    @Override
    public Optional<Version> getLatestTag() {
        try {
            final String tag = this.githubGateway.getLatestTag(getName());
            return Optional.ofNullable(tag == null ? null : Version.parse(tag));
        } catch (final GitHubException exception) {
            throw new RepositoryException(exception);
        }
    }

    @Override
    public String getName() {
        return this.fullName;
    }
}