package com.exasol.releaserobot.github;

import java.util.Map;

/**
 * Represents a GitHub release.
 */
public class GitHubRelease {
    private final String version;
    private final String header;
    private final String releaseLetter;
    private final String defaultBranchName;
    private final Map<String, String> assets;

    private GitHubRelease(final Builder builder) {
        this.version = builder.version;
        this.header = builder.header;
        this.releaseLetter = builder.releaseLetter;
        this.defaultBranchName = builder.defaultBranchName;
        this.assets = builder.assets;
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
     * Get assets.
     *
     * @return assets map
     */
    public Map<String, String> getAssets() {
        return this.assets;
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
     * A builder for {@link GitHubRelease}.
     */
    public static class Builder {
        private String version;
        private String header;
        private String releaseLetter = "";
        private String defaultBranchName;
        private Map<String, String> assets;

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
         * Set assets.
         *
         * @param assets map of assets
         * @return builder instance for fluent programming
         */
        public Builder assets(final Map<String, String> assets) {
            this.assets = assets;
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
            if (this.version == null || this.version.isEmpty()) {
                throw createExceptionWithInvalidField("version");
            }
            if (this.header == null || this.header.isEmpty()) {
                throw createExceptionWithInvalidField("header");
            }
            if (this.assets == null || this.assets.isEmpty()) {
                throw createExceptionWithInvalidField("assets");
            }
            if (this.defaultBranchName == null || this.defaultBranchName.isEmpty()) {
                throw createExceptionWithInvalidField("defaultBranchName");
            }
        }

        private IllegalArgumentException createExceptionWithInvalidField(final String fieldName) {
            return new IllegalArgumentException("E-GH-REL-1: Cannot create a GitHubRelease class, because '" + fieldName
                    + "' field is null or empty.");
        }
    }
}