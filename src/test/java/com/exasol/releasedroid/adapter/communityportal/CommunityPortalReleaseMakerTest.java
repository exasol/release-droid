package com.exasol.releasedroid.adapter.communityportal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.repository.ReleaseLetter;

class CommunityPortalReleaseMakerTest {
    @Test
    void testGetTags() {
        final String communityPortalTemplate = "{\n"
                + "  \"tags\": [\"Release Droid\", \"Java Tools\", \"Open Source\", \"GitHub\"],\n"
                + "  \"project description\": \"This is an open-source tool that helps integration team to automate releases.\"\n"
                + "}";
        final CommunityPortalReleaseMaker communityPortalReleaseMaker = new CommunityPortalReleaseMaker(null);
        final List<String> tags = communityPortalReleaseMaker.getTags(communityPortalTemplate);
        assertThat(tags, Matchers.containsInAnyOrder("Release Droid", "Java Tools", "Open Source", "GitHub"));
    }

    @Test
    void testRenderBody() {
        final String communityPortalTemplate = "{\n"
                + "  \"tags\": [\"Release Droid\", \"Java Tools\", \"Open Source\", \"GitHub\"],"
                + "  \"project description\": \"The [ElasticSearch Virtual Schema](https://github.com/exasol/elasticsearch-virtual-schema) provides an abstraction layer "
                + "that makes an external [ElasticSearch](https://www.elastic.co/) data source accessible from an Exasol database through regular "
                + "SQL commands. The contents of the external ElasticSearch data source are mapped to virtual tables "
                + "which look like, and can be queried as, any regular Exasol table.\"}";
        final ReleaseLetter releaseLetter = getReleaseLetter();
        final CommunityPortalReleaseMaker communityPortalReleaseMaker = new CommunityPortalReleaseMaker(null);
        final String body = communityPortalReleaseMaker.renderBody(communityPortalTemplate, releaseLetter,
                "ElasticSearch Virtual Schema 2.0.0",
                "https://github.com/exasol/elasticsearch-virtual-schema/releases/tag/2.0.0");
        assertThat(body, equalTo("<h2>About the project</h2>"
                + "<p>The <a href=\"https://github.com/exasol/elasticsearch-virtual-schema\" target=\"_blank\" rel=\"noopener\">ElasticSearch Virtual Schema</a> provides an abstraction layer that makes an external <a href=\"https://www.elastic.co/\" target=\"_blank\" rel=\"noopener\">ElasticSearch</a> data source accessible from an Exasol database through regular SQL commands. The contents of the external ElasticSearch data source are mapped to virtual tables which look like, and can be queried as, any regular Exasol table.</p>"
                + "<h2>New release</h2>"
                + "<p>The <code>SQL_DIALECT</code> property used when executing <code>CREATE VIRTUAL SCHEMA</code> from the Exasol database is obsolete from this version. Please do not provide this property anymore.</p>"
                + "<p>This release also adds support for a few scalar functions.</p>"
                + "<p>For more information check out the <a href=\"https://github.com/exasol/elasticsearch-virtual-schema/releases/tag/2.0.0\" target=\"_blank\" rel=\"noopener\">ElasticSearch Virtual Schema 2.0.0</a> release on GitHub.</p>"));
    }

    private ReleaseLetter getReleaseLetter() {
        final String body = "## Summary\n\n" //
                + "The 'SQL_DIALECT' property used when executing 'CREATE VIRTUAL SCHEMA' from the Exasol database is obsolete from this version. Please do not provide this property anymore.\n\n"
                + "This release also adds support for a few scalar functions.\n\n" //
                + "## Features\n\n" //
                + "* #1: feature 1\n" //
                + "* #2: feature 2";
        return ReleaseLetter.builder("file").body(body).build();
    }
}