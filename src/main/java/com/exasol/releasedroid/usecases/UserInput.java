package com.exasol.releasedroid.usecases;

import java.util.List;
import java.util.Objects;

import com.exasol.errorreporting.ExaError;

/**
 * This class stores user input.
 */
public class UserInput {
    private final String branch;
    private final Goal goal;
    private final List<PlatformName> platformNames;
    private final String repositoryName;
    private final String localPath;

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
    public List<PlatformName> getPlatformNames() {
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
     * Check if input contains a branch.
     *
     * @return true if a branch presents
     */
    public boolean hasBranch() {
        return (this.branch != null) && !this.branch.isEmpty();
    }

    /**
     * Get path to the local repository.
     * 
     * @return path to the local repository.
     */
    public String getLocalPath() {
        return this.localPath;
    }

    /**
     * Check if a path to a local repository exists.
     * 
     * @return true if a path to a local repository exists
     */
    public boolean hasLocalPath() {
        return this.localPath != null && !this.localPath.isEmpty();
    }

    private UserInput(final Builder builder) {
        this.branch = builder.branch;
        this.goal = builder.goal;
        this.platformNames = builder.platforms;
        this.repositoryName = builder.repositoryName;
        this.localPath = builder.localPath;
    }

    /**
     * Get a {@link UserInput} builder.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "UserInput{" + "branch='" + this.branch + '\'' + ", goal=" + this.goal + ", platformNames="
                + this.platformNames + ", repositoryName='" + this.repositoryName + '\'' + ", localPath='"
                + this.localPath + '\'' + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UserInput userInput = (UserInput) o;
        return Objects.equals(this.branch, userInput.branch) && this.goal == userInput.goal
                && this.platformNames.equals(userInput.platformNames)
                && this.repositoryName.equals(userInput.repositoryName)
                && Objects.equals(this.localPath, userInput.localPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.branch, this.goal, this.platformNames, this.repositoryName, this.localPath);
    }

    /**
     * Builder for {@link UserInput}.
     */
    public static final class Builder {
        private String branch;
        private Goal goal;
        private List<PlatformName> platforms;
        private String repositoryName;
        private String localPath;

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
         * @param platforms one or more platforms for validation or release.
         * @return builder instance for fluent programming
         */
        // [impl->dsn~users-set-release-platforms~1]
        public Builder platforms(final String... platforms) {
            this.platforms = PlatformName.toList(platforms);
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
         * Add a path to the root of a local repository.
         *
         * @param localPath path to the root of a local repository
         * @return builder instance for fluent programming
         */
        public Builder localPath(final String localPath) {
            this.localPath = localPath;
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
            validateLocalPath();
            return new UserInput(this);
        }

        private void validateLocalPath() {
            if ((this.localPath != null) && ((this.goal == Goal.RELEASE) || (this.branch != null))) {
                throw new IllegalArgumentException(ExaError.messageBuilder("E-RR-6")
                        .message("The 'local' argument can't be used together with 'branch' or RELEASE 'goal'.")
                        .toString());
            }
        }

        private void validateGoalAndBranch() {
            if ((this.goal == Goal.RELEASE) && (this.branch != null)) {
                throw new IllegalArgumentException(ExaError.messageBuilder("E-RR-1")
                        .message("Please, remove branch parameter if you want to make a release.").toString());
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
        }

        private void throwExceptionForMissingParameter(final String exceptionCode, final String parameter) {
            throw new IllegalArgumentException(ExaError.messageBuilder(exceptionCode)
                    .message("Please, specify a mandatory parameter {{parameter}} and re-run the Release Droid")
                    .parameter("parameter", parameter).toString());
        }
    }
}