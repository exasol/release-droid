package com.exasol.releaserobot.repository.maven;

import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * This class represents a Maven plugin.
 */
public class MavenPlugin {
    private final String artifactId;
    private final Xpp3Dom configuration;

    private MavenPlugin(final Builder builder) {
        this.artifactId = builder.artifactId;
        this.configuration = builder.configuration;
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
     * Get plugin's configuration.
     *
     * @return configuration
     */
    public Xpp3Dom getConfiguration() {
        return this.configuration;
    }

    /**
     * Check if the plugin has configuration.
     * 
     * @return true if configuration presents
     */
    public boolean hasConfiguration() {
        return this.configuration != null;
    }

    /**
     * Builder for the {@link MavenPlugin}.
     */
    public static class Builder {
        private String artifactId;
        private Xpp3Dom configuration;

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
         * Add configuration.
         *
         * @param configuration configuration as {@link Xpp3Dom} object
         * @return builder instance for fluent programming
         */
        public Builder configuration(final Xpp3Dom configuration) {
            this.configuration = configuration;
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