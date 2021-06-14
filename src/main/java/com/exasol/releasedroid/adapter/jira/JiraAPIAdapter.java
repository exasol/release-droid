package com.exasol.releasedroid.adapter.jira;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.PropertyReader;

/**
 * An adapter to communicate with Jira via API.
 */
public class JiraAPIAdapter implements JiraGateway {
    private static final String JIRA_URL = "https://www.exasol.com/support";
    private static final String JIRA_USERNAME_KEY = "jira_username";
    private static final String JIRA_PASSWORD_KEY = "jira_password";
    private final PropertyReader propertyReader;

    /**
     * Create a new instance of {@link JiraAPIAdapter}.
     *
     * @param propertyReader property reader
     */
    public JiraAPIAdapter(final PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    @Override
    public String createTicket(final String projectName, final String issueTypeName, final String summary,
            final String description) throws JiraException {
        try (final JiraRestClient client = createJiraClient()) {
            final Project project = getProject(projectName, client);
            final IssueType issueType = getIssueType(project, issueTypeName);
            final IssueInput issueInput = new IssueInputBuilder() //
                    .setProject(project) //
                    .setIssueType(issueType) //
                    .setSummary(summary) //
                    .setDescription(description) //
                    .build();
            final BasicIssue issue = createIssue(client, issueInput);
            return JIRA_URL + "/browse/" + issue.getKey();
        } catch (final IOException exception) {
            throw new JiraException(
                    ExaError.messageBuilder("E-RD-JIRA-4").message("Cannot create a Jira ticket.")
                            .mitigation("Please make sure you provided valid credentials.").toString(),
                    exception);
        }
    }

    private JiraRestClient createJiraClient() throws JiraException {
        final String username = this.propertyReader.readProperty(JIRA_USERNAME_KEY);
        final String password = this.propertyReader.readProperty(JIRA_PASSWORD_KEY);
        final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        try {
            return factory.createWithBasicHttpAuthentication(new URI(JIRA_URL), username, password);
        } catch (final URISyntaxException exception) {
            throw new JiraException(
                    ExaError.messageBuilder("E-RD-JIRA-3").message("Cannot authenticate a user in Jira.").toString(),
                    exception);
        }
    }

    private Project getProject(final String projectName, final JiraRestClient client) {
        return client.getProjectClient().getProject(projectName).claim();
    }

    private IssueType getIssueType(final Project project, final String issueTypeName) {
        for (final IssueType issueType : project.getIssueTypes()) {
            if (issueType.getName().equals(issueTypeName)) {
                return issueType;
            }
        }
        return null;
    }

    private BasicIssue createIssue(final JiraRestClient client, final IssueInput issueInput) {
        return client.getIssueClient().createIssue(issueInput).claim();
    }
}