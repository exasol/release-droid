package com.exasol.releasedroid.adapter.jira;

/**
 * The gateway to communicate with Jira.
 */
interface JiraGateway {
    /**
     * Create a new ticket.
     *
     * @param projectName   project name
     * @param issueTypeName issue type name
     * @param summary       summary
     * @param description   description
     * @return link to the created ticket
     * @throws JiraException if ticket cannot be created
     */
    String createTicket(final String projectName, final String issueTypeName, final String summary,
            final String description) throws JiraException;
}
