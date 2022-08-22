package com.exasol.releasedroid.adapter.maven;

import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.release.ReleaseMaker;
import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * This class is responsible for releases on Maven Central.
 */
public class MavenReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(MavenReleaseMaker.class.getName());
    private static final String RELEASE_ON_MAVEN_CENTRAL_WORKFLOW = "release_droid_release_on_maven_central.yml";
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
    public String makeRelease(final Repository repository, final Progress progress) throws ReleaseException {
        if (!(repository instanceof MavenRepository)) {
            throw new ReleaseException(ExaError.messageBuilder("E-RD-REP-29")
                    .message("Cannot make a Maven release for repository of type {{repositoryType}}")
                    .parameter("repositoryType", repository.getClass().getName()).toString());
        }
        LOGGER.fine("Releasing on Maven.");
        final String mavenRepoUrl = makeMavenRelease((MavenRepository) repository, progress);
        LOGGER.info(() -> "A MavenCentral release was published at: " + mavenRepoUrl);
        return mavenRepoUrl;
    }

    private String makeMavenRelease(final MavenRepository repository, final Progress progress) {
        final MavenPom mavenPom = repository.getMavenPom();
        if (mavenPom == null) {
            throw new ReleaseException(ExaError.messageBuilder("E-RD-REP-30")
                    .message("Repository {{repositoryName}} does not have Maven POM file")
                    .parameter("repositoryName", repository.getName()).toString());
        }
        try {
            this.githubGateway.executeWorkflow(repository.getName(), //
                    RELEASE_ON_MAVEN_CENTRAL_WORKFLOW, //
                    new WorkflowOptions().withProgress(progress));
            return "maven-central";
        } catch (final GitHubException exception) {
            throw new ReleaseException(exception);
        }
    }

    // [impl->dsn~estimate-duration~1]
    @Override
    public Estimation estimateDuration(final Repository repository) {
        return this.githubGateway.estimateDuration(repository.getName(), RELEASE_ON_MAVEN_CENTRAL_WORKFLOW);
    }
}