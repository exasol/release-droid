package com.exasol;

import static com.exasol.Platform.PlatformName.GITHUB;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.exasol.Platform.PlatformName;
import com.exasol.github.GitHubEntityFactory;
import com.exasol.repository.GitRepository;

/**
 * This class is the main entry point for calls to a Release Robot.
 */
public class ReleaseRobot {
    private static final Logger LOGGER = Logger.getLogger(ReleaseRobot.class.getName());
    private final String gitBranch;
    private final Goal goal;
    private final Set<PlatformName> platformNames;
    private final String repositoryName;
    private final String repositoryOwner;

    private ReleaseRobot(final Builder builder) {
        this.gitBranch = builder.gitBranch;
        this.goal = builder.goal;
        this.platformNames = builder.platforms;
        this.repositoryName = builder.repositoryName;
        this.repositoryOwner = builder.repositoryOwner;
    }

    /**
     * Main entry point for all Release Robot's calls.
     */
    public void run() {
        LOGGER.fine(() -> "Release Robot has received '" + this.goal + "' request for the project "
                + this.repositoryName + ".");
        try {
            final GitHubEntityFactory gitHubEntityFactory = new GitHubEntityFactory(this.repositoryOwner,
                    this.repositoryName);
            final GitRepository repository = gitHubEntityFactory.createGitHubGitRepository();
            final Set<Platform> platforms = createPlatforms(gitHubEntityFactory);
            final RepositoryHandler repositoryHandler = new RepositoryHandler(repository, platforms);
            if (this.goal == Goal.VALIDATE) {
                runValidation(repositoryHandler);
            } else {
                runRelease(repositoryHandler);
            }
        } catch (final RuntimeException exception) {
            LOGGER.severe(() -> "'" + this.goal + "' request failed. Cause: " + exception.getMessage());
        }
    }

    private Set<Platform> createPlatforms(final GitHubEntityFactory gitHubEntityFactory) {
        final Set<Platform> platforms = new HashSet<>();
        for (final PlatformName name : this.platformNames) {
            if (name == GITHUB) {
                final Platform gitHubPlatform = gitHubEntityFactory.createGitHubPlatform();
                platforms.add(gitHubPlatform);
            }
        }
        return platforms;
    }

    private void runValidation(final RepositoryHandler repositoryHandler) {
        if (hasBranch()) {
            repositoryHandler.validate();
        } else {
            repositoryHandler.validate(this.gitBranch);
        }
    }

    private boolean hasBranch() {
        return this.gitBranch == null || this.gitBranch.isEmpty();
    }

    private void runRelease(final RepositoryHandler repositoryHandler) {
        repositoryHandler.validate();
        repositoryHandler.release();
    }

    /**
     * Get a {@link ReleaseRobot} builder.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link ReleaseRobot}.
     */
    public static final class Builder {
        private String gitBranch;
        private Goal goal;
        private Set<PlatformName> platforms;
        private String repositoryName;
        private String repositoryOwner;

        /**
         * Add a git branch.
         *
         * @param gitBranch name of a branch to work with.
         * @return builder instance for fluent programming
         */
        public Builder gitBranch(final String gitBranch) {
            this.gitBranch = gitBranch;
            return this;
        }

        /**
         * Add a run goal.
         *
         * @param goal run goal. Supported goals: release, validate
         * @return builder instance for fluent programming
         */
        public Builder goal(final String goal) {
            this.goal = Goal.getGoal(goal);
            return this;
        }

        /**
         * Add release platforms.
         *
         * @param platforms one or more platforms for validation or release. Supported values: github
         * @return builder instance for fluent programming
         */
        public Builder platforms(final String... platforms) {
            this.platforms = PlatformName.toSet(platforms);
            return this;
        }

        /**
         * Add a name of a repository.
         *
         * @param repositoryName name of a target project from GitHub
         * @return builder instance for fluent programming
         */
        public Builder repositoryName(final String repositoryName) {
            this.repositoryName = repositoryName;
            return this;
        }

        /**
         * Add an owner of a GitHub repository.
         *
         * @param repositoryOwner an owner of a GitHub repository
         * @return builder instance for fluent programming
         */
        public Builder repositoryOwner(final String repositoryOwner) {
            this.repositoryOwner = repositoryOwner;
            return this;
        }

        /**
         * Create a new {@link ReleaseRobot} instance.
         *
         * @return new {@link ReleaseRobot} instance
         */
        public ReleaseRobot build() {
            validateMandatoryParameters();
            validateGoalAndBranch();
            return new ReleaseRobot(this);
        }

        private void validateGoalAndBranch() {
            if (this.goal == Goal.RELEASE && this.gitBranch != null) {
                throw new IllegalStateException("Please, remove branch parameter if you want to make a release.");
            }
        }

        private void validateMandatoryParameters() {
            if (this.goal == null) {
                throwExceptionForMissingParameter("goal");
            }
            if (this.platforms == null || this.platforms.isEmpty()) {
                throwExceptionForMissingParameter("platforms");
            }
            if (this.repositoryName == null) {
                throwExceptionForMissingParameter("repository name");
            }
            if (this.repositoryOwner == null) {
                throwExceptionForMissingParameter("repository owner");
            }
        }

        private void throwExceptionForMissingParameter(final String goal) {
            throw new IllegalStateException(MessageFormat
                    .format("Please, specify a mandatory parameter `{}` and re0run the Release Robot", goal));
        }
    }
}