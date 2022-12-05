package com.exasol.releasedroid.usecases.request;

import java.nio.file.Path;
import java.util.*;

/**
 * This class stores user input.
 */
public class UserInput {
    private String owner;
    private final String branch;
    private Goal goal;
    private final List<PlatformName> platformNames;
    private final String repositoryName;
    private final String localPath;
    private final Language language;
    private final boolean skipValidation;
    private final Optional<Path> releaseGuide;

    /**
     * Get a branch name.
     *
     * @return branch name
     */
    public String getBranch() {
        return this.branch;
    }

    /**
     * Check if input contains a branch.
     *
     * @return {@code true} if a branch is present
     */
    public boolean hasBranch() {
        return (this.branch != null) && !this.branch.isEmpty();
    }

    /**
     * Check if user wants to skip validation.
     *
     * @return {@code true} to skip validation
     */
    public boolean skipValidation() {
        return this.skipValidation;
    }

    /**
     * Get path to optional release guide. If empty then user did not request to create a release guide.
     *
     * @return optional path to release guide
     */
    public Optional<Path> releaseGuide() {
        return this.releaseGuide;
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
     * Check if input contains goal.
     *
     * @return {@code true} if a goal is present
     */
    public boolean hasGoal() {
        return this.goal != null;
    }

    /**
     * Set goal.
     *
     * @param goal goal
     */
    public void setGoal(final Goal goal) {
        this.goal = goal;
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
     * Check if input contains platforms.
     *
     * @return {@code true} if platforms are present
     */
    public boolean hasPlatforms() {
        return (this.platformNames != null) && !this.platformNames.isEmpty();
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
     * Check if input contains a repository name.
     *
     * @return {@code true} if a repository name is present
     */
    public boolean hasRepositoryName() {
        return (this.repositoryName != null) && !this.repositoryName.isEmpty();
    }

    /**
     * Get the owner.
     *
     * @return owner
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Check if input contains an owner.
     *
     * @return {@code true} if an owner is present
     */
    public boolean hasOwner() {
        return (this.owner != null) && !this.owner.isEmpty();
    }

    /**
     * Set the owner.
     *
     * @param owner owner
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    /**
     * Get path to the local repository.
     *
     * @return path to the local repository
     */
    public String getLocalPath() {
        return this.localPath;
    }

    /**
     * Check if a path to a local repository exists.
     *
     * @return {@code true} if a path to a local repository exists
     */
    public boolean hasLocalPath() {
        return (this.localPath != null) && !this.localPath.isEmpty();
    }

    /**
     * Get primary language of the repository.
     *
     * @return primary language of the repository
     */
    public Language getLanguage() {
        return this.language;
    }

    /**
     * Check if primary language of the repository is provided.
     *
     * @return {@code true} if primary language of the repository is provided
     */
    public boolean hasLanguage() {
        return this.language != null;
    }

    /**
     * Get a full repository name in format owner/repository.
     *
     * @return full repository name
     */
    public String getFullRepositoryName() {
        return getOwner() + "/" + getRepositoryName();
    }

    private UserInput(final Builder builder) {
        this.branch = builder.branch;
        this.goal = builder.goal;
        this.platformNames = builder.platforms;
        this.owner = builder.owner;
        this.repositoryName = builder.repositoryName;
        this.localPath = builder.localPath;
        this.language = builder.language;
        this.skipValidation = builder.skipValidation;
        this.releaseGuide = builder.releaseGuide;
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
    public int hashCode() {
        return Objects.hash(this.branch, this.goal, this.language, this.localPath, this.owner, this.platformNames,
                this.releaseGuide, this.repositoryName, this.skipValidation);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserInput other = (UserInput) obj;
        return Objects.equals(this.branch, other.branch) && (this.goal == other.goal)
                && (this.language == other.language) && Objects.equals(this.localPath, other.localPath)
                && Objects.equals(this.owner, other.owner) && Objects.equals(this.platformNames, other.platformNames)
                && Objects.equals(this.releaseGuide, other.releaseGuide)
                && Objects.equals(this.repositoryName, other.repositoryName)
                && (this.skipValidation == other.skipValidation);
    }

    @Override
    public String toString() {
        return "UserInput{" //
                + "owner='" + this.owner + '\'' //
                + ", branch='" + this.branch + '\'' //
                + ", goal=" + this.goal //
                + ", platformNames=" + this.platformNames //
                + ", repositoryName='" + this.repositoryName + '\'' //
                + ", localPath='" + this.localPath + '\'' //
                + ", language=" + this.language //
                + ", skipValidation=" + this.skipValidation //
                + ", releaseGuide=" + this.releaseGuide //
                + '}';
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
        private Language language;
        private String owner;
        private boolean skipValidation;
        private Optional<Path> releaseGuide = Optional.empty();

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
            if (goal != null) {
                this.goal = Goal.getGoal(goal);
            }
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
            if (platforms != null) {
                this.platforms = PlatformName.toList(platforms);
            }
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
         * Add an owner of a repository.
         *
         * @param owner owner of a repository
         * @return builder instance for fluent programming
         */
        public Builder owner(final String owner) {
            this.owner = owner;
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
         * Add a primary language of a repository.
         *
         * @param language primary language of a repository
         * @return builder instance for fluent programming
         */
        public Builder language(final String language) {
            if (language != null) {
                this.language = Language.getLanguage(language);
            }
            return this;
        }

        /**
         * Create a new {@link UserInput} instance.
         *
         * @return new {@link UserInput} instance
         */
        public UserInput build() {
            return new UserInput(this);
        }

        /**
         * Add a skip validation option.
         *
         * @param skipValidation skip validation
         * @return builder instance for fluent programming
         */
        public Builder skipValidation(final boolean skipValidation) {
            this.skipValidation = skipValidation;
            return this;
        }

        /**
         * Path of release guide to be generated by Release Droid
         *
         * @param path path of release guide to generate
         * @return builder instance for fluent programming
         */
        public Builder releaseGuide(final String path) {
            if (path != null) {
                this.releaseGuide = Optional.of(Path.of(path));
            }
            return this;
        }
    }
}