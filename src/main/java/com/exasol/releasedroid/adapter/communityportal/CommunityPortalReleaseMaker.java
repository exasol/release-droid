package com.exasol.releasedroid.adapter.communityportal;

import static com.exasol.releasedroid.adapter.communityportal.CommunityPortalConstants.COMMUNITY_CONFIG_PATH;

import java.util.logging.Logger;

import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.release.ReleaseMaker;
import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * This class is responsible for releasing on Exasol Community Portal.
 */
public class CommunityPortalReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(CommunityPortalReleaseMaker.class.getName());
    private final CommunityPortalGateway communityPortalGateway;

    /**
     * Create a new instance of {@link CommunityPortalReleaseMaker}.
     * 
     * @param communityPortalGateway instance of {@link CommunityPortalGateway}
     */
    public CommunityPortalReleaseMaker(final CommunityPortalGateway communityPortalGateway) {
        this.communityPortalGateway = communityPortalGateway;
    }

    @Override
    public String makeRelease(final Repository repository) throws ReleaseException {
        LOGGER.fine("Creating a draft of the release announcement on the Exasol Community Portal.");
        try {
            final var communityPost = getCommunityPost(repository);
            final String draftPostUrl = this.communityPortalGateway.sendDraftPost(communityPost);
            LOGGER.info(() -> "A community draft post was created at: " + draftPostUrl);
            return draftPostUrl;
        } catch (final CommunityPortalException exception) {
            throw new ReleaseException(exception);
        }
    }

    // [impl->dsn~extract-release-changes-description-from-release-letter~1]
    private CommunityPost getCommunityPost(final Repository repository) {
        final String version = repository.getVersion();
        final CommunityConfig config = getConfig(repository);
        final var releaseLetter = repository.getReleaseLetter(version);
        final String header = config.getCommunityProjectName() + " " + version;
        final String gitHubReleaseLink = buildGitHubReleaseLink(repository, version);
        final String body = renderBody(header, config.getCommunityProjectDescription(),
                releaseLetter.getSummary().orElseThrow(), gitHubReleaseLink);
        return CommunityPost.builder() //
                .boardId("ProductNews") //
                .header(header + " released") //
                .tags(config.getCommunityTags()) //
                .body(body) //
                .build();
    }

    private String buildGitHubReleaseLink(final Repository repository, final String version) {
        return "https://github.com/" + repository.getName() + "/releases/tag/" + version;
    }

    // [impl->dsn~extract-project-description-from-file~1]
    private CommunityConfig getConfig(final Repository repository) {
        return CommunityConfigParser.parse(repository.getSingleFileContentAsString(COMMUNITY_CONFIG_PATH));
    }

    private String renderBody(final String header, final String projectDescription, final String changesDescription,
            final String gitHubReleaseLink) {
        final var communityPostRenderer = new CommunityPostRenderer();
        return communityPostRenderer.renderCommunityPostBody(header, projectDescription, changesDescription,
                gitHubReleaseLink);
    }
}