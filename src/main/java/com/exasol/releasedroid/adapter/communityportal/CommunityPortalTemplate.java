package com.exasol.releasedroid.adapter.communityportal;

import java.util.List;

/**
 * Represents a community portal template file with the information for releasing on the Exasol Community Portal.
 */
public class CommunityPortalTemplate {
    private final String projectName;
    private final String projectDescription;
    private final List<String> tags;

    /**
     * Instantiate a new Community portal template.
     *
     * @param projectName        project name
     * @param projectDescription project description
     * @param tags               tags
     */
    public CommunityPortalTemplate(final String projectName, final String projectDescription, final List<String> tags) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.tags = tags;
    }

    /**
     * Get the project name.
     *
     * @return project name
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * Get the tags.
     *
     * @return tags
     */
    public List<String> getTags() {
        return this.tags;
    }

    /**
     * Get the project description.
     *
     * @return project description
     */
    public String getProjectDescription() {
        return this.projectDescription;
    }

    /**
     * Check if has a project name.
     *
     * @return true if has a project name
     */
    public boolean hasProjectName() {
        return this.projectName != null && !this.projectName.isEmpty();
    }

    /**
     * Check if has a project description.
     *
     * @return true if has a project description
     */
    public boolean hasProjectDescription() {
        return this.projectDescription != null && !this.projectDescription.isEmpty();
    }

    /**
     * Check if has tags.
     *
     * @return true if has tags
     */
    public boolean hasTags() {
        return this.tags != null && !this.tags.isEmpty();
    }
}