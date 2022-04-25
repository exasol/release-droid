package com.exasol.releasedroid.adapter.jira;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.exasol.releasedroid.usecases.PropertyReader;

@ExtendWith(MockitoExtension.class)
class JiraAPIAdapterTest {

    @Mock
    private PropertyReader propertyReaderMock;
    private JiraAPIAdapter jiraAPIAdapter;

    @BeforeEach
    void setup() {
        lenient().when(propertyReaderMock.readProperty("jira_username", false)).thenReturn("wrong-user");
        lenient().when(propertyReaderMock.readProperty("jira_password", false)).thenReturn("wrong-password");
        jiraAPIAdapter = new JiraAPIAdapter(propertyReaderMock);
    }

    @Test
    void testCreatingJiraClientSucceeds() throws JiraException {
        assertDoesNotThrow(() -> jiraAPIAdapter.createJiraClient());
    }

    @Test
    void testCreatingJiraClientReturnsClient() throws JiraException {
        final JiraRestClient client = jiraAPIAdapter.createJiraClient();
        assertThat(client, notNullValue());
    }
}
