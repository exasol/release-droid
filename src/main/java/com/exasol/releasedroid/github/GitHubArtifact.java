package com.exasol.releasedroid.github;

public class GitHubArtifact {
    private final String id;
    private final String name;
    private final String archiveDownloadUrl;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getArchiveDownloadUrl() {
        return this.archiveDownloadUrl;
    }

    private GitHubArtifact(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.archiveDownloadUrl = builder.archiveDownloadUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String archiveDownloadUrl;

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder archiveDownloadUrl(final String archiveDownloadUrl) {
            this.archiveDownloadUrl = archiveDownloadUrl;
            return this;
        }

        public GitHubArtifact build() {
            return new GitHubArtifact(this);
        }
    }
}
