package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.progress.Progress.plural;

import java.net.URL;
import java.util.*;

/**
 * Informations about a release after release has been created
 */
public class GitHubReleaseInfo {

    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * @param repository repository name
     * @param version    version of the release
     * @return url pointing to the release
     */
    public static String getTagUrl(final String repository, final String version) {
        return "https://github.com/" + repository + "/releases/tag/" + version;
    }

    private GitHubReleaseInfo() {
        // use builder
    }

    private String repositoryName;
    private String version;
    private final List<String> additionalTags = new ArrayList<>();
    private boolean isDraft;
    private URL htmlUrl;

    /**
     * @return url pointing to the release
     */
    public String getTagUrl() {
        return getTagUrl(this.repositoryName, this.version);
    }

    /**
     * @return {@code true} if the release is in draft status, yet
     */
    public boolean isDraft() {
        return this.isDraft;
    }

    /**
     * @return HTML URL of the release enabling human visitors to display the release in a browser
     */
    public URL getHtmlUrl() {
        return this.htmlUrl;
    }

    /**
     * @return string representing additional git tags if such tags have been created for the release, an empty string
     *         otherwise.
     */
    public Optional<String> additionalTagsReport() {
        final int n = this.additionalTags.size();
        if (n < 1) {
            return Optional.empty();
        }
        return Optional.of(String.format("%d additional tag%s: ", //
                n, plural(n), String.join(", ", this.additionalTags)));
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

        public Builder additionalTags(final List<String> tags) {
            this.info.additionalTags.addAll(tags);
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
