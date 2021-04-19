package com.exasol.releasedroid.adapter.communityportal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.ReleaseLetterParser;
import com.exasol.releasedroid.usecases.repository.Repository;

class CommunityPortalReleaseMakerTest {
    @Test
    void testGetCommunityPost() {
        final Repository repositoryMock = Mockito.mock(Repository.class);
        final String communityPortalTemplate = "{\n"
                + "  \"tags\": [\"Release Droid\", \"Java Tools\", \"Open Source\", \"GitHub\"],"
                + " \"project name\": \"Virtual Schema for ElasticSearch\"," //
                + "  \"project description\": \"The [ElasticSearch Virtual Schema](https://github.com/exasol/elasticsearch-virtual-schema) provides an abstraction layer "
                + "that makes an external [ElasticSearch](https://www.elastic.co/) data source accessible from an Exasol database through regular "
                + "SQL commands. The contents of the external ElasticSearch data source are mapped to virtual tables "
                + "which look like, and can be queried as, any regular Exasol table.\"}";
        final ReleaseLetter releaseLetter = getReleaseLetter();
        when(repositoryMock.getName()).thenReturn("exasol/elasticsearch-virtual-schema");
        when(repositoryMock.getVersion()).thenReturn("2.0.0");
        when(repositoryMock.getSingleFileContentAsString("community_portal_post_template.json"))
                .thenReturn(communityPortalTemplate);
        when(repositoryMock.getReleaseLetter("2.0.0")).thenReturn(releaseLetter);
        final CommunityPortalReleaseMaker communityPortalReleaseMaker = new CommunityPortalReleaseMaker(null);
        final CommunityPost communityPost = communityPortalReleaseMaker.getCommunityPost(repositoryMock);
        final String body = "<h2>About the project</h2>"
                + "<p>The <a href=\"https://github.com/exasol/elasticsearch-virtual-schema\" target=\"_blank\" rel=\"noopener\">ElasticSearch Virtual Schema</a> provides an abstraction layer that makes an external <a href=\"https://www.elastic.co/\" target=\"_blank\" rel=\"noopener\">ElasticSearch</a> data source accessible from an Exasol database through regular SQL commands. The contents of the external ElasticSearch data source are mapped to virtual tables which look like, and can be queried as, any regular Exasol table.</p>"
                + "<h2>New release</h2>"
                + "<p>The <code>SQL_DIALECT</code> property used when executing <code>CREATE VIRTUAL SCHEMA</code> from the Exasol database is obsolete from this version. Please do not provide this property anymore.</p>"
                + "<p>This release also adds support for a few scalar functions.</p>"
                + "<p>For more information check out the <a href=\"https://github.com/exasol/elasticsearch-virtual-schema/releases/tag/2.0.0\" target=\"_blank\" rel=\"noopener\">Virtual Schema for ElasticSearch 2.0.0</a> release on GitHub.</p>";
        assertAll(
                () -> assertThat(communityPost.getTags(),
                        containsInAnyOrder("Release Droid", "Java Tools", "Open Source", "GitHub")),
                () -> assertThat(communityPost.getHeader(), equalTo("Virtual Schema for ElasticSearch 2.0.0 released")),
                () -> assertThat(communityPost.getBoardId(), equalTo("ProductNews")),
                () -> assertThat(communityPost.getBody(), equalTo(body)));

    }

    private ReleaseLetter getReleaseLetter() {
        final String body = "# Virtual Schema for ElasticSearch 2.0.0, released 2021-04-06\n\n"
                + "Code name: Remove SQL_DIALECT property\n\n" //
                + "## Summary\n\n" //
                + "The 'SQL_DIALECT' property used when executing 'CREATE VIRTUAL SCHEMA' from the Exasol database is obsolete from this version. Please do not provide this property anymore.\n\n"
                + "This release also adds support for a few scalar functions.\n\n" //
                + "## Features\n\n" //
                + "* #1: feature 1\n" //
                + "* #2: feature 2";
        final ReleaseLetterParser releaseLetterParser = new ReleaseLetterParser("file", body);
        return releaseLetterParser.parse();
    }
}