package com.exasol.releasedroid.adapter.github;

import java.util.logging.Logger;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
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
    public void makeRelease(final Repository repository) throws ReleaseException {
        LOGGER.fine("Releasing on GitHub.");
        final String version = repository.getVersion();
        final ReleaseLetter releaseLetter = repository.getReleaseLetter(version);
        final String body = releaseLetter.getBody().orElse("");
        final String header = releaseLetter.getHeader().orElse(version);
        final GitHubRelease release = GitHubRelease.builder().repositoryName(repository.getName()).version(version)
                .header(header).releaseLetter(body).build();
        try {
            this.githubGateway.createGithubRelease(release);
        } catch (final GitHubException exception) {
            throw new ReleaseException(exception);
        }
    }
}