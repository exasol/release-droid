package com.exasol.releasedroid.adapter.communityportal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.ReleaseLetterParser;
import com.exasol.releasedroid.usecases.repository.Repository;

class CommunityPortalReleaseMakerTest {
    @Test
    void testGetCommunityPost() throws CommunityPortalException {
        final Repository repositoryMock = Mockito.mock(Repository.class);
        final CommunityPortalGateway gatewayMock = Mockito.mock(CommunityPortalGateway.class);
        final String communityPortalTemplate = "{\n"
                + "  \"tags\": [\"Release Droid\", \"Java Tools\", \"Open Source\", \"GitHub\"],"
                + " \"project name\": \"Virtual Schema for ElasticSearch\"," //
                + "  \"project description\": \"Here is a project description.\"}";
        final ReleaseLetter releaseLetter = getReleaseLetter();
        when(repositoryMock.getName()).thenReturn("exasol/elasticsearch-virtual-schema");
        when(repositoryMock.getVersion()).thenReturn("2.0.0");
        when(repositoryMock.getSingleFileContentAsString("community_portal_post_template.json"))
                .thenReturn(communityPortalTemplate);
        when(repositoryMock.getReleaseLetter("2.0.0")).thenReturn(releaseLetter);
        final CommunityPortalReleaseMaker communityPortalReleaseMaker = new CommunityPortalReleaseMaker(gatewayMock);
        communityPortalReleaseMaker.makeRelease(repositoryMock);
        final String body = "<h2>About the project</h2>" //
                + "<p>Here is a project description.</p>\n" //
                + "<h2>New release</h2>" //
                + "<p>Here is a summary.</p>\n" //
                + "<p>For more information check out "
                + "the <a href=\"https://github.com/exasol/elasticsearch-virtual-schema/releases/tag/2.0.0\" "
                + "target=\"_blank\" rel=\"noopener\">Virtual Schema for ElasticSearch 2.0.0</a> release on GitHub.</p>\n";
        final CommunityPost communityPost = CommunityPost.builder().body(body)
                .header("Virtual Schema for ElasticSearch 2.0.0 released") //
                .tags(List.of("Release Droid", "Java Tools", "Open Source", "GitHub")) //
                .boardId("ProductNews").build();
        verify(gatewayMock, times(1)).sendDraftPost(communityPost);
    }

    private ReleaseLetter getReleaseLetter() {
        final String body = "# Virtual Schema for ElasticSearch 2.0.0, released 2021-04-06\n\n"
                + "Code name: Remove SQL_DIALECT property\n\n" //
                + "## Summary\n\n" //
                + "Here is a summary.\n\n" //
                + "## Features\n\n" //
                + "* #1: feature 1\n" //
                + "* #2: feature 2";
        final ReleaseLetterParser releaseLetterParser = new ReleaseLetterParser("file", body);
        return releaseLetterParser.parse();
    }

    @Test
    void testGetCommunityPostThrowsException() throws CommunityPortalException {
        final Repository repositoryMock = Mockito.mock(Repository.class);
        final CommunityPortalGateway gatewayMock = Mockito.mock(CommunityPortalGateway.class);
        final String communityPortalTemplate = "{\n"
                + "  \"tags\": [\"Release Droid\", \"Java Tools\", \"Open Source\", \"GitHub\"],"
                + " \"project name\": \"Virtual Schema for ElasticSearch\"," //
                + "  \"project description\": \"Here is a project description.\"}";
        final ReleaseLetter releaseLetter = getReleaseLetter();
        when(repositoryMock.getName()).thenReturn("exasol/elasticsearch-virtual-schema");
        when(repositoryMock.getVersion()).thenReturn("2.0.0");
        when(repositoryMock.getSingleFileContentAsString("community_portal_post_template.json"))
                .thenReturn(communityPortalTemplate);
        when(repositoryMock.getReleaseLetter("2.0.0")).thenReturn(releaseLetter);
        final CommunityPortalReleaseMaker communityPortalReleaseMaker = new CommunityPortalReleaseMaker(gatewayMock);
        doThrow(CommunityPortalException.class).when(gatewayMock).sendDraftPost(any());
        assertThrows(ReleaseException.class, () -> communityPortalReleaseMaker.makeRelease(repositoryMock));
    }
}