package com.exasol.releasedroid.adapter.maven;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents maven pom.xml file.
 */
public class MavenPom {
    private final String artifactId;
    private final String groupId;
    private final Map<String, String> properties;
    private final Map<String, MavenPlugin> plugins;
    private final String projectDescription;
    private final String projectURL;

    private MavenPom(final Builder builder) {
        this.artifactId = builder.artifactId;
        this.groupId = builder.groupId;
        this.properties = builder.properties;
        this.plugins = builder.plugins;
        this.projectDescription = builder.projectDescription;
        this.projectURL = builder.projectURL;
    }

    /**
     * Get project's artifact id.
     *
     * @return artifact id
     */
    public String getArtifactId() {
        return this.artifactId;
    }

    /**
     * Check if artifact id present.
     *
     * @return {@code true} if artifactId present
     */
    public boolean hasArtifactId() {
        return (this.artifactId != null) && !this.artifactId.isEmpty();
    }

    /**
     * Check if group id present.
     *
     * @return {@code true} if group id present
     */
    public boolean hasGroupId() {
        return (this.groupId != null) && !this.groupId.isEmpty();
    }

    /**
     * Get project's group id.
     *
     * @return group id
     */
    public String getGroupId() {
        return this.groupId;
    }

    /**
     * Get properties map.
     *
     * @return properties map
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Check if properties present.
     *
     * @return {@code true} if properties present
     */
    public boolean hasProperties() {
        return !this.properties.isEmpty();
    }

    /**
     * Get a map of maven plugins.
     *
     * @return map with plugins' names and {@link MavenPlugin}
     */
    public Map<String, MavenPlugin> getPlugins() {
        return this.plugins;
    }

    /**
     * Check if plugins present.
     *
     * @return {@code true} if plugins present
     */
    public boolean hasPlugins() {
        return !this.plugins.isEmpty();
    }

    /**
     * Check if project description present.
     *
     * @return {@code true} if project description present
     */
    public boolean hasProjectDescription() {
        return (this.projectDescription != null) && !this.projectDescription.isEmpty();
    }

    /**
     * Check if project URL present.
     *
     * @return true if project URL present
     */
    public boolean hasProjectURL() {
        return (this.projectURL != null) && !this.projectURL.isEmpty();
    }

    /**
     * Get a {@link MavenPom} builder.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for the {@link MavenPom}.
     */
    public static class Builder {
        private String artifactId;
        private String groupId;
        private Map<String, String> properties = new HashMap<>();
        private Map<String, MavenPlugin> plugins = new HashMap<>();
        private String projectDescription;
        private String projectURL;

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
         * Add group id.
         *
         * @param groupId group id
         * @return builder instance for fluent programming
         */
        public Builder groupId(final String groupId) {
            this.groupId = groupId;
            return this;
        }

        /**
         * Add properties map.
         *
         * @param properties properties as a map
         * @return builder instance for fluent programming
         */
        public Builder properties(final Map<String, String> properties) {
            this.properties = properties;
            return this;
        }

        /**
         * Create a new instance of {@link MavenPom}.
         *
         * @return new instance of {@link MavenPom}
         */
        public MavenPom build() {
            return new MavenPom(this);
        }

        /**
         * Add plugins.
         *
         * @param plugins map with plugins' names and {@link MavenPlugin}
         * @return builder instance for fluent programming
         */
        public Builder plugins(final Map<String, MavenPlugin> plugins) {
            this.plugins = plugins;
            return this;
        }

        /**
         * Add a project description.
         *
         * @param projectDescription the project description
         * @return builder instance for fluent programming
         */
        public Builder projectDescription(final String projectDescription) {
            this.projectDescription = projectDescription;
            return this;
        }

        /**
         * Add a project URL.
         *
         * @param projectURL project url
         * @return builder instance for fluent programming
         */
        public Builder projectURL(final String projectURL) {
            this.projectURL = projectURL;
            return this;
        }
    }
}