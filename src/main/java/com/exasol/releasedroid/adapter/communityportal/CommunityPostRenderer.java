package com.exasol.releasedroid.adapter.communityportal;

/**
 * Renders a Community Post Body.
 */
public class CommunityPostRenderer {
    /**
     * Render an Exasol release announcement community post body in the format the portal supports.
     * 
     * @param projectNameAndVersion name and version of the released project
     * @param projectDescription    project description
     * @param changesDescription    description of the changes in the current release
     * @param gitHubReleaseLink     link to the release on the GitHub
     * @return rendered community post body
     */
    public String renderCommunityPostBody(final String projectNameAndVersion, final String projectDescription,
            final String changesDescription, final String gitHubReleaseLink) {
        final var builder = new StringBuilder();
        builder.append("<h2>About the project</h2>") //
                .append(formatParagraph(projectDescription)) //
                .append("<h2>New release</h2>") //
                .append(formatParagraph(changesDescription)) //
                .append("<p>For more information check out the <a href=\"") //
                .append(gitHubReleaseLink) //
                .append("\" target=\"_blank\" rel=\"noopener\">") //
                .append(projectNameAndVersion) //
                .append("</a> release on GitHub.</p>");
        return builder.toString();
    }

    private String formatParagraph(final String projectDescription) {
        final var builder = new StringBuilder();
        builder.append("<p>");
        final char[] chars = projectDescription.toCharArray();
        for (var i = 0; i < chars.length; ++i) {
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
        final var text = new StringBuilder();
        ++i;
        while (i < chars.length && chars[i] != ']') {
            text.append(chars[i]);
            ++i;
        }
        i += 2;
        final var link = new StringBuilder();
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