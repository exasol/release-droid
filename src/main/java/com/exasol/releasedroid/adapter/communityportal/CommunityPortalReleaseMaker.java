package com.exasol.releasedroid.adapter.communityportal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.release.ReleaseMaker;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * This class is responsible for releasing on Exasol Community Portal.
 */
public class CommunityPortalReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(CommunityPortalReleaseMaker.class.getName());
    private final CommunityPortalGateway communityPortalGateway;

    public CommunityPortalReleaseMaker(final CommunityPortalGateway communityPortalGateway) {
        this.communityPortalGateway = communityPortalGateway;
    }

    @Override
    public void makeRelease(final Repository repository) throws ReleaseException {
        LOGGER.fine("Creating a draft of the release announcement on the Exasol Community Portal.");
        try {
            final var communityPost = getCommunityPost(repository);
            this.communityPortalGateway.createDraftPost(communityPost);
        } catch (final CommunityPortalException exception) {
            throw new ReleaseException(exception);
        }
    }

    protected CommunityPost getCommunityPost(final Repository repository) {
        final String version = repository.getVersion();
        final var communityPortalTemplate = repository
                .getSingleFileContentAsString("community_portal_post_template.json");
        final var releaseLetter = repository.getReleaseLetter(version);
        final String header = getProjectName(communityPortalTemplate) + " " + version;
        final List<String> tags = getTags(communityPortalTemplate);
        final String gitHubReleaseLink = "https://github.com/" + repository.getName() + "/releases/tag/" + version;
        final String body = renderBody(communityPortalTemplate, releaseLetter, header, gitHubReleaseLink);
        return CommunityPost.builder() //
                .boardId("ProductNews") //
                .header(header + " released") //
                .tags(tags) //
                .body(body) //
                .build();
    }

    private String getProjectName(final String communityPortalTemplate) {
        return new JSONObject(communityPortalTemplate).getString("project name");
    }

    private List<String> getTags(final String communityPortalTemplate) {
        final var tagsArray = new JSONObject(communityPortalTemplate).getJSONArray("tags");
        final List<String> tags = new ArrayList<>();
        for (var i = 0; i < tagsArray.length(); ++i) {
            tags.add(tagsArray.getString(i));
        }
        return tags;
    }

    private String renderBody(final String communityPortalTemplate, final ReleaseLetter releaseLetter,
            final String header, final String gitHubReleaseLink) {
        final var projectDescription = new JSONObject(communityPortalTemplate).getString("project description");
        final String changesDescription = releaseLetter.getSummary().orElseThrow();
        final var communityPostRenderer = new CommunityPostRenderer();
        return communityPostRenderer.renderCommunityPostBody(header, projectDescription, changesDescription,
                gitHubReleaseLink);
    }
}