package com.exasol.releasedroid.adapter.maven;

import java.util.logging.Logger;

import com.exasol.releasedroid.adapter.github.GitHubException;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.release.ReleaseMaker;
import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * This class is responsible for releases on Maven Central.
 */
public class MavenReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(MavenReleaseMaker.class.getName());
    private final GitHubGateway githubGateway;

    /**
     * Create a new instance of {@link MavenReleaseMaker}.
     *
     * @param githubGateway instance of {@link GitHubGateway}
     */
    public MavenReleaseMaker(final GitHubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    @Override
    // [impl->dsn~create-new-maven-release~1]
    public void makeRelease(final Repository repository) throws ReleaseException {
        LOGGER.fine("Releasing on Maven.");
        try {
            this.githubGateway.executeWorkflow(repository.getName(), "release_droid_release_on_maven_central.yml");
        } catch (final GitHubException exception) {
            throw new ReleaseException(exception);
        }
    }
}