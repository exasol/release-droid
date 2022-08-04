package com.exasol.releasedroid.adapter.github;

import java.net.URL;

public class GitHubReleaseInfo {

    public static Builder builder() {
        return new Builder();
    }

    private GitHubReleaseInfo() {
        // use builder
    }

    private String repositoryName;
    private String version;
    private boolean isDraft;
    private URL htmlUrl;

    public String getTagUrl() {
        return "https://github.com/" + this.repositoryName + "/releases/tag/" + this.version;
    }

    public boolean isDraft() {
        return this.isDraft;
    }

    public URL getHtmlUrl() {
        return this.htmlUrl;
    }

    static class Builder {
        GitHubReleaseInfo info = new GitHubReleaseInfo();

        public Builder repositoryName(final String value) {
            this.info.repositoryName = value;
            return this;
        }

        public Builder version(final String value) {
            this.info.version = value;
            return this;
        }

        public Builder draft(final boolean value) {
            this.info.isDraft = value;
            return this;
        }

        public Builder htmlUrl(final URL value) {
            this.info.htmlUrl = value;
            return this;
        }

        public GitHubReleaseInfo build() {
            return this.info;
        }
    }
}