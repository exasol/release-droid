package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.github.GitHubConstants.GITHUB_UPLOAD_ASSETS_WORKFLOW_PATH;

import java.util.Optional;
import java.util.logging.Logger;

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
        final String releaseUrl = createGitHubRelease(repository);
        LOGGER.info(() -> "A GitHub release was created at: " + releaseUrl);
        return releaseUrl;
    }

    private String createGitHubRelease(final Repository repository) {
        final String version = repository.getVersion();
        final GitHubRelease release = createReleaseModel(repository, version);
        try {
            this.githubGateway.createGithubRelease(release);
            return "https://github.com/" + repository.getName() + "/releases/tag/" + version;
        } catch (final GitHubException exception) {
            throw new ReleaseException(exception);
        }
    }

    private GitHubRelease createReleaseModel(final Repository repository, final String version) {
        final ReleaseLetter releaseLetter = repository.getReleaseLetter(version);
        Optional<String> header = releaseLetter.getHeader();
        if (header.isEmpty()) {
            throw new IllegalStateException("Release header must not be empty.");
        }
        final String body = releaseLetter.getBody().orElse("");
        final boolean uploadReleaseAssets = checkIfUploadAssetsWorkflowExists(repository);
        return GitHubRelease.builder() //
                .repositoryName(repository.getName()) //
                .version(version) //
                .header(version + ": " + header.get()) //
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