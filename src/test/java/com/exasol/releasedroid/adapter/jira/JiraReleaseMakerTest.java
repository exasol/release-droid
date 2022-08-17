package com.exasol.releasedroid.adapter.jira;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.FILE_SEPARATOR;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_DIRECTORY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.PropertyReader;
import com.exasol.releasedroid.usecases.PropertyReaderImpl;

class JiraReleaseMakerTest {

    final JiraReleaseMaker jiraReleaseMaker = new JiraReleaseMaker(null);

    @Test
    void testGetHumanReadableNameWithRepositoryOwner() {
        assertThat(this.jiraReleaseMaker.getDefaultProjectName("exasol/test-repository-name"),
                equalTo("Test Repository Name"));
    }

    @Test
    void testGetHumanReadableNameWithoutRepositoryOwner() {
        assertThat(this.jiraReleaseMaker.getDefaultProjectName("test-repository-name"),
                equalTo("Test Repository Name"));
    }

    void manual() throws JiraException {
        final PropertyReader reader = new PropertyReaderImpl(RELEASE_DROID_DIRECTORY + FILE_SEPARATOR + "credentials");
        final JiraGateway jiraGateway = new JiraAPIAdapter(reader);
        final var projectName = "EXACOMM";
        final var issueTypeName = "Task";
        final var summary = "(only for testing -- please ignore)";
        final String description = "This ticket is only for testing -- please ignore." //
                + "I will close the ticket, soon.";
        final String link = jiraGateway.createTicket(projectName, issueTypeName, summary, description);
        System.out.println("Created ticket " + link);
    }

}