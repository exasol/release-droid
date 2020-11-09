package com.exasol.releaserobot.usecases;

import java.util.Set;

/**
 * This class stores user input.
 */
public class UserInput {
    private final String branch;
    private final Goal goal;
    private final Set<PlatformName> platformNames;
    private final String repositoryName;
    private final String repositoryOwner;

    /**
     * Get a branch name.
     *
     * @return branch name
     */
    public String getBranch() {
        return this.branch;
    }

    /**
     * Get a goal.
     *
     * @return goal
     */
    public Goal getGoal() {
        return this.goal;
    }

    /**
     * Get platforms' names.
     *
     * @return platforms' names
     */
    public Set<PlatformName> getPlatformNames() {
        return this.platformNames;
    }

    /**
     * Get a repository name.
     *
     * @return repository name
     */
    public String getRepositoryName() {
        return this.repositoryName;
    }

    /**
     * Get a repository owner.
     *
     * @return repository owner
     */
    public String getRepositoryOwner() {
        return this.repositoryOwner;
    }

    /**
     * Check if input contains a branch.
     *
     * @return true if a branch presents
     */
    public boolean hasBranch() {
        return (this.branch != null) && !this.branch.isEmpty();
    }

    private UserInput(final Builder builder) {
        this.branch = builder.branch;
        this.goal = builder.goal;
        this.platformNames = builder.platforms;
        this.repositoryName = builder.repositoryName;
        this.repositoryOwner = builder.repositoryOwner;
    }

    /**
     * Get a {@link UserInput} builder.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link UserInput}.
     */
    public static final class Builder {
        private String branch;
        private Goal goal;
        private Set<PlatformName> platforms;
        private String repositoryName;
        private String repositoryOwner;

        /**
         * Add a branch.
         *
         * @param branch name of a branch to work with.
         * @return builder instance for fluent programming
         */
        // [impl->dsn~users-can-set-git-branch-for-validation~1]
        public Builder branch(final String branch) {
            this.branch = branch;
            return this;
        }

        /**
         * Add a run goal.
         *
         * @param goal run goal. Supported goals: release, validate
         * @return builder instance for fluent programming
         */
        // [impl->dsn~users-set-run-goal~1]
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
        // [impl->dsn~users-set-release-platforms~1]
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
        // [impl->dsn~users-set-project~1]
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
         * Create a new {@link UserInput} instance.
         *
         * @return new {@link UserInput} instance
         */
        public UserInput build() {
            validateMandatoryParameters();
            validateGoalAndBranch();
            return new UserInput(this);
        }

        private void validateGoalAndBranch() {
            if ((this.goal == Goal.RELEASE) && (this.branch != null)) {
                throw new IllegalArgumentException(
                        "E-RR-1: Please, remove branch parameter if you want to make a release.");
            }
        }

        private void validateMandatoryParameters() {
            if (this.goal == null) {
                throwExceptionForMissingParameter("E-RR-2", "goal");
            }
            if ((this.platforms == null) || this.platforms.isEmpty()) {
                throwExceptionForMissingParameter("E-RR-3", "platforms");
            }
            if (this.repositoryName == null) {
                throwExceptionForMissingParameter("E-RR-4", "repository name");
            }
            if (this.repositoryOwner == null) {
                throwExceptionForMissingParameter("E-RR-5", "repository owner");
            }
        }

        private void throwExceptionForMissingParameter(final String exceptionCode, final String parameter) {
            final String message = exceptionCode + ": Please, specify a mandatory parameter `" + parameter
                    + "` and re-run the Release Robot";
            throw new IllegalArgumentException(message);
        }
    }

    public String getRepositoryFullName() {
        return this.getRepositoryOwner() + "/" + this.repositoryName;
    }
}