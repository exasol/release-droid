package com.exasol.releasedroid.adapter.communityportal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
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
            final CommunityPost communityPost = getCommunityPost(repository);
            this.communityPortalGateway.createDraftPost(communityPost);
        } catch (final RuntimeException exception) {
            throw new ReleaseException(exception);
        }
    }

    private CommunityPost getCommunityPost(final Repository repository) {
        final String version = repository.getVersion();
        final String communityPortalTemplate = repository
                .getSingleFileContentAsString("community_portal_post_template.json");
        final ReleaseLetter releaseLetter = repository.getReleaseLetter(version);
        final String header = getHeader(releaseLetter) + " " + version + " released";
        final List<String> tags = getTags(communityPortalTemplate);
        final String gitHubReleaseLink = "https://github.com/" + repository.getName() + "/releases/tag/" + version;
        final String body = renderBody(communityPortalTemplate, releaseLetter, header, gitHubReleaseLink);
        return CommunityPost.builder() //
                .boardId("ProductNews") //
                .header(header) //
                .tags(tags) //
                .body(body) //
                .build();
    }

    private String getHeader(final ReleaseLetter releaseLetter) {
        return releaseLetter.getHeader().orElseThrow();
    }

    protected List<String> getTags(final String communityPortalTemplate) {
        final JSONArray tagsArray = new JSONObject(communityPortalTemplate).getJSONArray("tags");
        final List<String> tags = new ArrayList<>();
        for (int i = 0; i < tagsArray.length(); ++i) {
            tags.add(tagsArray.getString(i));
        }
        return tags;
    }

    protected String renderBody(final String communityPortalTemplate, final ReleaseLetter releaseLetter,
            final String header, final String gitHubReleaseLink) {
        final String projectDescription = new JSONObject(communityPortalTemplate).getString("project description");
        final String changesDescription = releaseLetter.getSummary().orElseThrow();
        return renderBodyInHtmlFormat(projectDescription, changesDescription, header, gitHubReleaseLink);
    }

    private String renderBodyInHtmlFormat(final String projectDescription, final String changesDescription,
            final String header, final String gitHubReleaseLink) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<h2>About the project</h2>");
        builder.append(formatParagraph(projectDescription));
        builder.append("<h2>New release</h2>");
        builder.append(formatParagraph(changesDescription));
        builder.append("<p>For more information check out the <a href=\"");
        builder.append(gitHubReleaseLink);
        builder.append("\" target=\"_blank\" rel=\"noopener\">");
        builder.append(header);
        builder.append("</a> release on GitHub.</p>");
        return builder.toString();
    }

    private String formatParagraph(final String projectDescription) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<p>");
        final char[] chars = projectDescription.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            final char ch = chars[i];
            if (ch == '[') {
                i = renderLink(builder, chars, i);
            } else if (ch == '\'' && (i == 0 || chars[i - 1] == ' ')) {
                i = renderCodeReference(builder, chars, i);
            } else if (ch == '\n') {
                if (i == 0 || chars[i - 1] != '\n') {
                    builder.append("</p><p>");
                }
            } else {
                builder.append(ch);
            }
        }
        builder.append("</p>");
        return builder.toString();
    }

    private int renderLink(final StringBuilder builder, final char[] chars, int i) {
        final StringBuilder text = new StringBuilder();
        ++i;
        while (i < chars.length && chars[i] != ']') {
            text.append(chars[i]);
            ++i;
        }
        i += 2;
        final StringBuilder link = new StringBuilder();
        while (i < chars.length && chars[i] != ')') {
            link.append(chars[i]);
            ++i;
        }
        builder.append("<a href=\"") //
                .append(link) //
                .append("\" target=\"_blank\" rel=\"noopener\">") //
                .append(text) //
                .append("</a>");
        return i;
    }

    private int renderCodeReference(final StringBuilder builder, final char[] chars, int i) {
        builder.append("<code>");
        ++i;
        while (i < chars.length) {
            final char ch = chars[i];
            ++i;
            if (ch == '\'' && (i == chars.length || chars[i] == ' ')) {
                break;
            } else {
                builder.append(ch);
            }
        }
        builder.append("</code> ");
        return i;
    }
}