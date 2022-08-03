package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.github.GitHubConstants.GITHUB_UPLOAD_ASSETS_WORKFLOW_PATH;

import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.release.ReleaseMaker;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * This class is responsible for releases on GitHub.
 */
public class GitHubReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(GitHubReleaseMaker.class.getName());
    private final GitHubGateway githubGateway;

    /**
     * Create a new {@link GitHubReleaseMaker}.
     *
     * @param githubGateway instance of {@link GitHubGateway}
     */
    public GitHubReleaseMaker(final GitHubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    @Override
    // [impl->dsn~create-new-github-release~1]
    public String makeRelease(final Repository repository) throws ReleaseException {
        LOGGER.fine("Releasing on GitHub.");
        final GitHubReleaseInfo info = createGitHubRelease(repository);
        final String releaseUrl = info.getTagUrl();
        LOGGER.info(() -> "A GitHub release was created at: " + releaseUrl);
        if (info.isDraft()) {
            LOGGER.info(() -> "Please do not forget to finalize the draft at: " + info.getHtmlUrl());
        }
        return releaseUrl;
    }

    private GitHubReleaseInfo createGitHubRelease(final Repository repository) {
        final String version = repository.getVersion();
        final GitHubRelease release = createReleaseModel(repository, version);
        try {
            return this.githubGateway.createGithubRelease(release);
        } catch (final GitHubException exception) {
            throw new ReleaseException(exception);
        }
    }

    private GitHubRelease createReleaseModel(final Repository repository, final String version) {
        final ReleaseLetter releaseLetter = repository.getReleaseLetter(version);
        final String header = releaseLetter.getHeader().orElse("");
        if (header.isEmpty()) {
            throw new IllegalStateException(ExaError.messageBuilder("E-RD-GH-28") //
                    .message("Release header must not be empty.") //
                    .mitigation("Please provide release letter with non-empty header.") //
                    .toString());
        }
        final String body = releaseLetter.getBody().orElse("");
        final boolean uploadReleaseAssets = checkIfUploadAssetsWorkflowExists(repository);
        return GitHubRelease.builder() //
                .repositoryName(repository.getName()) //
                .version(version) //
                .header(version + ": " + header) //
                .releaseLetter(body) //
                .uploadAssets(uploadReleaseAssets) //
                .build();
    }

    private boolean checkIfUploadAssetsWorkflowExists(final Repository repository) {
        try {
            repository.getSingleFileContentAsString(GITHUB_UPLOAD_ASSETS_WORKFLOW_PATH);
            return true;
        } catch (final RepositoryException exception) {
            return false;
        }
    }
}