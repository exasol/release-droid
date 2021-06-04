package com.exasol.releasedroid.usecases.response;

import java.util.List;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.request.Goal;
import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * The release droid response.
 */
public class ReleaseDroidResponse {
    private final String fullRepositoryName;
    private final Goal goal;
    private final String branch;
    private final String localRepositoryPath;
    private final List<PlatformName> platformNames;
    private final List<Report> reports;

    private ReleaseDroidResponse(final Builder builder) {
        this.fullRepositoryName = builder.fullRepositoryName;
        this.goal = builder.goal;
        this.branch = builder.branch;
        this.localRepositoryPath = builder.localRepositoryPath;
        this.platformNames = builder.platformNames;
        this.reports = builder.reports;
    }

    /**
     * Get the full repository name.
     *
     * @return full repository name
     */
    public String getFullRepositoryName() {
        return this.fullRepositoryName;
    }

    /**
     * Get the goal.
     *
     * @return goal
     */
    public Goal getGoal() {
        return this.goal;
    }

    /**
     * Get the branch.
     *
     * @return branch
     */
    public String getBranch() {
        return this.branch;
    }

    /**
     * Get the path to a local repository.
     *
     * @return local path
     */
    public String getLocalRepositoryPath() {
        return this.localRepositoryPath;
    }

    /**
     * Get the platform names.
     *
     * @return platform names
     */
    public List<PlatformName> getPlatformNames() {
        return this.platformNames;
    }

    /**
     * Get the reports.
     *
     * @return reports
     */
    public List<Report> getReports() {
        return this.reports;
    }

    /**
     * Create a new builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link ReleaseDroidResponse}.
     */
    public static class Builder {
        private String fullRepositoryName;
        private Goal goal;
        private String branch;
        private String localRepositoryPath;
        private List<PlatformName> platformNames;
        private List<Report> reports;

        /**
         * Add full repository name.
         *
         * @param fullRepositoryName full repository name
         * @return builder
         */
        public Builder fullRepositoryName(final String fullRepositoryName) {
            this.fullRepositoryName = fullRepositoryName;
            return this;
        }

        /**
         * Add goal.
         *
         * @param goal goal
         * @return builder
         */
        public Builder goal(final Goal goal) {
            this.goal = goal;
            return this;
        }

        /**
         * Add branch.
         *
         * @param branch branch
         * @return builder
         */
        public Builder branch(final String branch) {
            this.branch = branch;
            return this;
        }

        /**
         * Add path to a local repository.
         *
         * @param localRepositoryPath local repository path
         * @return builder
         */
        public Builder localRepositoryPath(final String localRepositoryPath) {
            this.localRepositoryPath = localRepositoryPath;
            return this;
        }

        /**
         * Add platform names.
         *
         * @param platformNames platform names
         * @return builder
         */
        public Builder platformNames(final List<PlatformName> platformNames) {
            this.platformNames = platformNames;
            return this;
        }

        /**
         * Add reports.
         *
         * @param reports reports
         * @return builder
         */
        public Builder reports(final List<Report> reports) {
            this.reports = reports;
            return this;
        }

        /**
         * Build release droid response.
         *
         * @return release droid response
         */
        public ReleaseDroidResponse build() {
            return new ReleaseDroidResponse(this);
        }
    }
}