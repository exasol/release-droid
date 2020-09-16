package com.exasol.repository.maven;

/**
 * Represents maven pom.xml file.
 */
public class MavenPom {
    private final String artifactId;
    private final String version;
    private final String deliverableName;

    private MavenPom(final Builder builder) {
        this.artifactId = builder.artifactId;
        this.version = builder.version;
        this.deliverableName = builder.deliverableName;
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
     * Get version.
     * 
     * @return version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Get deliverable name.
     * 
     * @return deliverable name
     */
    public String getDeliverableName() {
        return this.deliverableName;
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
        private String version;
        private String deliverableName;

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
         * Add deliverable name.
         *
         * @param deliverableName deliverable name
         * @return builder instance for fluent programming
         */
        public Builder deliverableName(final String deliverableName) {
            this.deliverableName = deliverableName;
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
    }
}