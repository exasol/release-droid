package com.exasol.releasedroid.adapter.jira;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class JiraReleaseMakerTest {
    final JiraReleaseMaker jiraReleaseMaker = new JiraReleaseMaker(null);

    @Test
    void testGetHumanReadableNameWithRepositoryOwner() {
        assertThat(this.jiraReleaseMaker.getHumanReadableName("exasol/test-repository-name"),
                equalTo("Test Repository Name"));
    }

    @Test
    void testGetHumanReadableNameWithoutRepositoryOwner() {
        assertThat(this.jiraReleaseMaker.getHumanReadableName("test-repository-name"), equalTo("Test Repository Name"));
    }
}