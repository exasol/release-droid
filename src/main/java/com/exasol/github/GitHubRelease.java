package com.exasol.github;

import java.util.Map;

/**
 * Represents a GitHub release.
 */
public class GitHubRelease {
    private final String version;
    private final String header;
    private final String releaseLetter;
    private final Map<String, String> assets;

    private GitHubRelease(final Builder builder) {
        this.version = builder.version;
        this.header = builder.header;
        this.releaseLetter = builder.releaseLetter;
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

    public String getVersion() {
        return this.version;
    }

    public String getHeader() {
        return this.header;
    }

    public String getReleaseLetter() {
        return this.releaseLetter;
    }

    public Map<String, String> getAssets() {
        return this.assets;
    }

    public static class Builder {
        private String version;
        private String header;
        private String releaseLetter = "";
        private Map<String, String> assets;

        public Builder version(final String version) {
            this.version = version;
            return this;
        }

        public Builder header(final String header) {
            this.header = header;
            return this;
        }

        public Builder releaseLetter(final String releaseLetter) {
            this.releaseLetter = releaseLetter;
            return this;
        }

        public Builder assets(final Map<String, String> assets) {
            this.assets = assets;
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
        }

        private GitHubException createExceptionWithInvalidField(final String fieldName) {
            return new GitHubException("E-GH-REL-1: Cannot create a GitHubRelease class, because '" + fieldName
                    + "' field is null or empty.");
        }
    }
}