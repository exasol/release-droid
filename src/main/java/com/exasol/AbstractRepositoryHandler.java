package com.exasol;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.platform.GitHubRepository;
import com.exasol.validation.ProjectValidator;

/**
 * This class handles repositories.
 */
public abstract class AbstractRepositoryHandler implements RepositoryHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepositoryHandler.class);
    private final Set<ReleasePlatform> platforms;
    protected final GitHubRepository repository;

    /**
     * Create a new instance of {@link AbstractRepositoryHandler}.
     * 
     * @param repository in instance of {@link GitHubRepository}
     * @param platforms one or more {@link ReleasePlatform}
     */
    public AbstractRepositoryHandler(final GitHubRepository repository, final Set<ReleasePlatform> platforms) {
        this.repository = repository;
        this.platforms = platforms;
    }

    @Override
    public void validate() {
        LOGGER.info("Validation of project started.");
        final ProjectValidator projectValidator = new ProjectValidator(this.repository, getVersion());
        projectValidator.validatePlatformIndependent();
        for (final ReleasePlatform platform : this.platforms) {
            projectValidator.validatePlatform(platform);
        }
        LOGGER.info("Validation successfully finished.");
    }

    @Override
    public void release() {
        LOGGER.info("Release of project started.");
        final ReleaseMaker releaseMaker = new ReleaseMaker(this.repository, getVersion());
        for (final ReleasePlatform platform : this.platforms) {
            releaseMaker.makeRelease(platform);
        }
        LOGGER.info("Release successfully finished.");
    }
}