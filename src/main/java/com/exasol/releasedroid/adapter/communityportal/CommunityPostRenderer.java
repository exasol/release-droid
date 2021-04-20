package com.exasol.releasedroid.adapter.communityportal;

import java.util.LinkedList;
import java.util.Queue;

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
        final Queue<Character> chars = createQueue(projectDescription);
        Character previous = null;
        while (!chars.isEmpty()) {
            final char cur = chars.poll();
            if (cur == '[') {
                builder.append(renderLink(chars));
            } else if (cur == '\'' && (previous == null || previous == ' ')) {
                builder.append(renderCodeReference(chars));
            } else if (cur == '\n') {
                if (previous == null || previous != '\n') {
                    builder.append("</p><p>");
                }
            } else {
                builder.append(cur);
            }
            previous = cur;
        }
        builder.append("</p>");
        return builder.toString();
    }

    private Queue<Character> createQueue(final String projectDescription) {
        final Queue<Character> queue = new LinkedList<>();
        for (final char c : projectDescription.toCharArray()) {
            queue.add(c);
        }
        return queue;
    }

    private String renderLink(final Queue<Character> chars) {
        final var text = new StringBuilder();
        while (!chars.isEmpty() && chars.peek() != ']') {
            text.append(chars.poll());
        }
        chars.remove(); // remove `]`
        chars.remove(); // remove '('
        final var link = new StringBuilder();
        while (!chars.isEmpty() && chars.peek() != ')') {
            link.append(chars.poll());
        }
        chars.remove(); // remove `)`
        return "<a href=\"" + link + "\" target=\"_blank\" rel=\"noopener\">" + text + "</a>";
    }

    private String renderCodeReference(final Queue<Character> chars) {
        final var builder = new StringBuilder();
        builder.append("<code>");
        while (!chars.isEmpty()) {
            final char ch = chars.poll();
            if (ch == '\'' && (chars.isEmpty() || chars.peek() == ' ')) {
                break;
            } else {
                builder.append(ch);
            }
        }
        builder.append("</code>");
        return builder.toString();
    }
}