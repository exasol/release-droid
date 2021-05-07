package com.exasol.releasedroid.usecases.repository;

import java.util.List;

import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * This class represents a release config file.
 */
public class ReleaseConfig {
    private final List<String> communityTags;
    private final String communityProjectName;
    private final String communityProjectDescription;
    private final List<PlatformName> releasePlatforms;

    private ReleaseConfig(final Builder builder) {
        this.communityTags = builder.communityTags;
        this.communityProjectName = builder.communityProjectName;
        this.communityProjectDescription = builder.communityProjectDescription;
        this.releasePlatforms = builder.releasePlatforms;
    }

    /**
     * Get the community tags.
     *
     * @return community tags
     */
    public List<String> getCommunityTags() {
        return this.communityTags;
    }

    /**
     * Check if community tags present.
     *
     * @return true if community tags present
     */
    public boolean hasCommunityTags() {
        return this.communityTags != null && !this.communityTags.isEmpty();
    }

    /**
     * Get the community project name.
     *
     * @return community project name
     */
    public String getCommunityProjectName() {
        return this.communityProjectName;
    }

    /**
     * Check if a community project name presents.
     *
     * @return true if a community project name presents
     */
    public boolean hasCommunityProjectName() {
        return this.communityProjectName != null && !this.communityProjectName.isEmpty();
    }

    /**
     * Get the community project description.
     *
     * @return community project description
     */
    public String getCommunityProjectDescription() {
        return this.communityProjectDescription;
    }

    /**
     * Check if a community project description presents.
     *
     * @return true if a community project description presents
     */
    public boolean hasCommunityProjectDescription() {
        return this.communityProjectDescription != null && !this.communityProjectDescription.isEmpty();
    }

    /**
     * Get the release platforms.
     *
     * @return release platforms
     */
    public List<PlatformName> getReleasePlatforms() {
        return this.releasePlatforms;
    }

    /**
     * Check if release platforms present.
     *
     * @return true if release platforms present
     */
    public boolean hasReleasePlatforms() {
        return this.releasePlatforms != null && !this.releasePlatforms.isEmpty();
    }

    /**
     * Create a {@link ReleaseConfig} builder.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The builder for {@link ReleaseConfig}.
     */
    public static class Builder {
        private List<String> communityTags;
        private String communityProjectName;
        private String communityProjectDescription;
        private List<PlatformName> releasePlatforms;

        /**
         * Add community tags.
         *
         * @param communityTags community tags
         * @return builder
         */
        public Builder communityTags(final List<String> communityTags) {
            this.communityTags = communityTags;
            return this;
        }

        /**
         * Add a community project name.
         *
         * @param communityProjectName community project name
         * @return builder
         */
        public Builder communityProjectName(final String communityProjectName) {
            this.communityProjectName = communityProjectName;
            return this;
        }

        /**
         * Add a community project description.
         *
         * @param communityProjectDescription community project description
         * @return builder
         */
        public Builder communityProjectDescription(final String communityProjectDescription) {
            this.communityProjectDescription = communityProjectDescription;
            return this;
        }

        /**
         * Add release platforms.
         *
         * @param releasePlatforms release platforms
         * @return builder
         */
        public Builder releasePlatforms(final List<String> releasePlatforms) {
            this.releasePlatforms = PlatformName.toList(releasePlatforms.toArray(new String[0]));
            return this;
        }

        /**
         * Build a {@link ReleaseConfig}.
         *
         * @return release config
         */
        public ReleaseConfig build() {
            return new ReleaseConfig(this);
        }
    }
}