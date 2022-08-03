package com.exasol.releasedroid.usecases.repository;

import java.util.*;

import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * This class represents a release config file.
 */
public class ReleaseConfig {
    private final List<PlatformName> releasePlatforms;
    private final Optional<String> language;

    private ReleaseConfig(final Builder builder) {
        this.releasePlatforms = builder.releasePlatforms;
        this.language = Optional.ofNullable(builder.language);
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
     * Get the main programming language.
     *
     * @return language
     */
    public Optional<String> getLanguage() {
        return this.language;
    }

    /**
     * Check if release platforms present.
     *
     * @return true if release platforms present
     */
    public boolean hasReleasePlatforms() {
        return (this.releasePlatforms != null) && !this.releasePlatforms.isEmpty();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        final ReleaseConfig that = (ReleaseConfig) o;
        return Objects.equals(this.releasePlatforms, that.releasePlatforms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.releasePlatforms);
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
        private List<PlatformName> releasePlatforms;
        private String language;

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
         * Add main programming language.
         *
         * @param value main programming language
         * @return main programming language
         */
        public Builder language(final String value) {
            this.language = value;
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