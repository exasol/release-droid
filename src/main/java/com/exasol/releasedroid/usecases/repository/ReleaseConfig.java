package com.exasol.releasedroid.usecases.repository;

import java.util.*;

import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * This class represents a release config file.
 */
public class ReleaseConfig {
    private List<PlatformName> releasePlatforms = Collections.emptyList();
    private Optional<String> language = Optional.empty();

    private ReleaseConfig() {
        // use builder!
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

    @Override
    public int hashCode() {
        return Objects.hash(this.language, this.releasePlatforms);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReleaseConfig other = (ReleaseConfig) obj;
        return Objects.equals(this.language, other.language)
                && Objects.equals(this.releasePlatforms, other.releasePlatforms);
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
        private final ReleaseConfig config = new ReleaseConfig();

        /**
         * Add release platforms.
         *
         * @param releasePlatforms release platforms
         * @return builder
         */
        public Builder releasePlatforms(final List<String> releasePlatforms) {
            this.config.releasePlatforms = PlatformName.toList(releasePlatforms.toArray(new String[0]));
            return this;
        }

        /**
         * Add main programming language.
         *
         * @param value main programming language
         * @return builder
         */
        public Builder language(final String value) {
            this.config.language = Optional.of(value);
            return this;
        }

        /**
         * Build a {@link ReleaseConfig}.
         *
         * @return release config
         */
        public ReleaseConfig build() {
            return this.config;
        }
    }
}