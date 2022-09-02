package com.exasol.releasedroid.adapter.github;

import java.util.ArrayList;
import java.util.List;

import com.exasol.errorreporting.ExaError;

/**
 * Represents a GitHub release.
 */
public class GitHubRelease {
    private String repositoryName;
    private String version;
    private String header;
    private String releaseLetter;
    private boolean uploadAssets;
    private final List<String> additionalTags = new ArrayList<>();

    /**
     * @return new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return version of the release
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * @return release header
     */
    public String getHeader() {
        return this.header;
    }

    /**
     * @return release letter
     */
    public String getReleaseLetter() {
        return this.releaseLetter;
    }

    /**
     * @return repository name
     */
    public String getRepositoryName() {
        return this.repositoryName;
    }

    /**
     * @return {@link true} if upload assets required
     */
    public boolean hasUploadAssets() {
        return this.uploadAssets;
    }

    public List<String> additionalTags() {
        return this.additionalTags;
    }

    // ---------------------------------------------------------------

    /**
     * A builder for {@link GitHubRelease}.
     */
    public static class Builder {
        private final GitHubRelease release = new GitHubRelease();

        /**
         * @param repositoryName repository name
         * @return builder instance for fluent programming
         */
        public Builder repositoryName(final String repositoryName) {
            this.release.repositoryName = repositoryName;
            return this;
        }

        /**
         * @param version release version
         * @return builder instance for fluent programming
         */
        public Builder version(final String version) {
            this.release.version = version;
            return this;
        }

        /**
         * @param header release header
         * @return builder instance for fluent programming
         */
        public Builder header(final String header) {
            this.release.header = header;
            return this;
        }

        /**
         * @param releaseLetter release letter
         * @return builder instance for fluent programming
         */
        public Builder releaseLetter(final String releaseLetter) {
            this.release.releaseLetter = releaseLetter;
            return this;
        }

        /**
         * @param value {@code true} if release is designated to upload assets
         * @return builder instance for fluent programming
         */
        public Builder uploadAssets(final boolean value) {
            this.release.uploadAssets = value;
            return this;
        }

        /**
         * @param value additional tag (aka. "git reference") pointing to the release
         * @return builder instance for fluent programming
         */
        public Builder addTag(final String value) {
            this.release.additionalTags.add(value);
            return this;
        }

        /**
         * @return new instance of {@link GitHubRelease}
         */
        public GitHubRelease build() {
            validateFields();
            return this.release;
        }

        private void validateFields() {
            if ((this.release.repositoryName == null) || this.release.repositoryName.isEmpty()) {
                throw createExceptionWithInvalidField("repositoryName");
            }
            if ((this.release.version == null) || this.release.version.isEmpty()) {
                throw createExceptionWithInvalidField("version");
            }
            if ((this.release.header == null) || this.release.header.isEmpty()) {
                throw createExceptionWithInvalidField("header");
            }
        }

        private IllegalArgumentException createExceptionWithInvalidField(final String fieldName) {
            return new IllegalArgumentException(ExaError.messageBuilder("E-RD-GH-9")
                    .message("Cannot create a GitHubRelease class, because {{fieldName}} field is null or empty.")
                    .parameter("fieldName", fieldName).toString());
        }
    }
}