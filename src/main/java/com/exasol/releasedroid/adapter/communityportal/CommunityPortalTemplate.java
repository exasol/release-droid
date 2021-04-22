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
}