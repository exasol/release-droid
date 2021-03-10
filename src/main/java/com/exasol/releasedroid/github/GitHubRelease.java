package com.exasol.releasedroid.github;

import com.exasol.errorreporting.ExaError;

/**
 * Represents a GitHub release.
 */
public class GitHubRelease {
    private final String repositoryName;
    private final String version;
    private final String header;
    private final String releaseLetter;
    private final String defaultBranchName;

    private GitHubRelease(final Builder builder) {
        this.repositoryName = builder.repositoryName;
        this.version = builder.version;
        this.header = builder.header;
        this.releaseLetter = builder.releaseLetter;
        this.defaultBranchName = builder.defaultBranchName;
    }

    /**
     * Create a new {@link Builder}.
     *
     * @return new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get a version.
     *
     * @return version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Get a release header.
     *
     * @return header
     */
    public String getHeader() {
        return this.header;
    }

    /**
     * Get a release letter.
     *
     * @return release letter
     */
    public String getReleaseLetter() {
        return this.releaseLetter;
    }

    /**
     * Get default branch name.
     *
     * @return default branch name
     */
    public String getDefaultBranchName() {
        return this.defaultBranchName;
    }

    /**
     * Get repository name.
     *
     * @return repository name
     */
    public String getRepositoryName() {
        return this.repositoryName;
    }

    /**
     * A builder for {@link GitHubRelease}.
     */
    public static class Builder {
        private String repositoryName;
        private String version;
        private String header;
        private String releaseLetter = "";
        private String defaultBranchName;

        /**
         * Set a repository name.
         *
         * @param repositoryName repository name
         * @return builder instance for fluent programming
         */
        public Builder repositoryName(final String repositoryName) {
            this.repositoryName = repositoryName;
            return this;
        }

        /**
         * Set a version.
         *
         * @param version release version
         * @return builder instance for fluent programming
         */
        public Builder version(final String version) {
            this.version = version;
            return this;
        }

        /**
         * Set a header
         *
         * @param header release header
         * @return builder instance for fluent programming
         */
        public Builder header(final String header) {
            this.header = header;
            return this;
        }

        /**
         * Set a release letter.
         *
         * @param releaseLetter release letter
         * @return builder instance for fluent programming
         */
        public Builder releaseLetter(final String releaseLetter) {
            this.releaseLetter = releaseLetter;
            return this;
        }

        /**
         * Set a default branch name.
         *
         * @param defaultBranchName default branch name
         * @return builder instance for fluent programming
         */
        public Builder defaultBranchName(final String defaultBranchName) {
            this.defaultBranchName = defaultBranchName;
            return this;
        }

        /**
         * Create a new instance if {@link GitHubRelease}.
         *
         * @return instance of {@link GitHubRelease}
         */
        public GitHubRelease build() {
            validateFields();
            return new GitHubRelease(this);
        }

        private void validateFields() {
            if ((this.repositoryName == null) || this.repositoryName.isEmpty()) {
                throw createExceptionWithInvalidField("repositoryName");
            }
            if ((this.version == null) || this.version.isEmpty()) {
                throw createExceptionWithInvalidField("version");
            }
            if ((this.header == null) || this.header.isEmpty()) {
                throw createExceptionWithInvalidField("header");
            }
            if ((this.defaultBranchName == null) || this.defaultBranchName.isEmpty()) {
                throw createExceptionWithInvalidField("defaultBranchName");
            }
        }

        private IllegalArgumentException createExceptionWithInvalidField(final String fieldName) {
            return new IllegalArgumentException(ExaError.messageBuilder("E-GH-REL-1")
                    .message("Cannot create a GitHubRelease class, because {{fieldName}} field is null or empty.")
                    .parameter("fieldName", fieldName).toString());
        }
    }
}