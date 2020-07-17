package com.exasol;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.platform.GitHubRepository;
import com.exasol.validation.ProjectValidator;
import com.exasol.validation.ProjectValidatorJava;

/**
 * This class handles a release process for Java-based projects.
 */
public class ReleaseMakerJava implements ReleaseMaker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseMakerJava.class);
    private static final String REPOSITORY_OWNER = "exasol";
    private final String repositoryName;
    private final Set<ReleasePlatform> platforms;

    /**
     * Create a new instance of {@link ReleaseMakerJava}.
     * 
     * @param repositoryName name of a target project from GitHub
     * @param platforms one or more {@link ReleasePlatform}
     */
    public ReleaseMakerJava(final String repositoryName, final Set<ReleasePlatform> platforms) {
        this.repositoryName = repositoryName;
        this.platforms = platforms;
    }

    @Override
    public void validate() {
        LOGGER.info("Validation of Java project started.");
        final GitHubRepository repository = GitHubRepository.getAnonymousGitHubRepository(REPOSITORY_OWNER,
                this.repositoryName);
        final ProjectValidator projectValidator = new ProjectValidatorJava(repository);
        projectValidator.validatePlatformIndependent();
        for (final ReleasePlatform releasePlatform : this.platforms) {
            projectValidator.validatePlatform(releasePlatform);
        }
    }

    @Override
    public void release() {
        LOGGER.info("Release of Java project started.");
        final GitHubRepository repository = GitHubRepository.getLogInGitHubRepository(REPOSITORY_OWNER,
                this.repositoryName);
    }
}