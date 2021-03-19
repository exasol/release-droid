package com.exasol.releasedroid.repository;

/**
 * This class represents a Maven plugin.
 */
public class MavenPlugin {
    private final String artifactId;
    private final String version;

    private MavenPlugin(final Builder builder) {
        this.artifactId = builder.artifactId;
        this.version = builder.version;
    }

    /**
     * Get a {@link MavenPlugin} builder.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get plugin's artifact id.
     *
     * @return artifact id
     */
    public String getArtifactId() {
        return this.artifactId;
    }

    /**
     * Check if the plugin has a version specified.
     *
     * @return version
     */
    public boolean hasVersion() {
        return this.version != null && !this.version.isEmpty();
    }

    /**
     * Get plugin's version.
     *
     * @return version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Builder for the {@link MavenPlugin}.
     */
    public static class Builder {
        private String artifactId;
        private String version;

        /**
         * Add artifact id.
         *
         * @param artifactId artifact id
         * @return builder instance for fluent programming
         */
        public Builder artifactId(final String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        /**
         * Add version.
         *
         * @param version version
         * @return builder instance for fluent programming
         */
        public Builder version(final String version) {
            this.version = version;
            return this;
        }

        /**
         * Create a new instance of {@link MavenPlugin}.
         *
         * @return new instance of {@link MavenPlugin}
         */
        public MavenPlugin build() {
            return new MavenPlugin(this);
        }
    }
}