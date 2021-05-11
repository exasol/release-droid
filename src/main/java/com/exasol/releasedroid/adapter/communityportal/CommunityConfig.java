package com.exasol.releasedroid.adapter.communityportal;

import java.util.List;
import java.util.Objects;

/**
 * This class represents a community config file.
 */
public class CommunityConfig {
    private final List<String> communityTags;
    private final String communityProjectName;
    private final String communityProjectDescription;

    private CommunityConfig(final Builder builder) {
        this.communityTags = builder.communityTags;
        this.communityProjectName = builder.communityProjectName;
        this.communityProjectDescription = builder.communityProjectDescription;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CommunityConfig that = (CommunityConfig) o;
        return Objects.equals(this.communityTags, that.communityTags)
                && Objects.equals(this.communityProjectName, that.communityProjectName)
                && Objects.equals(this.communityProjectDescription, that.communityProjectDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.communityTags, this.communityProjectName, this.communityProjectDescription);
    }

    @Override
    public String toString() {
        return "CommunityConfig{" + "communityTags=" + this.communityTags + ", communityProjectName='"
                + this.communityProjectName + '\'' + ", communityProjectDescription='"
                + this.communityProjectDescription + '\'' + '}';
    }

    /**
     * Create a {@link CommunityConfig} builder.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The builder for {@link CommunityConfig}.
     */
    public static class Builder {
        private List<String> communityTags;
        private String communityProjectName;
        private String communityProjectDescription;

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
         * Build a {@link CommunityConfig}.
         *
         * @return release config
         */
        public CommunityConfig build() {
            return new CommunityConfig(this);
        }
    }
}