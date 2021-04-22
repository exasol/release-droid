package com.exasol.releasedroid.adapter.communityportal;

import java.util.Map;

import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Renders a Community Post Body.
 */
public class CommunityPostRenderer {
    private final Parser mdParser = Parser.builder().build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder() //
            .attributeProviderFactory(context -> new LinkAttributeProvider()).build();

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
        final String lastParagraph = "For more information check out the [" + projectNameAndVersion + "]("
                + gitHubReleaseLink + ") release on GitHub.";
        return "<h2>About the project</h2>" + //
                renderHtml(projectDescription) + //
                "<h2>New release</h2>" + //
                renderHtml(changesDescription) + //
                renderHtml(lastParagraph);
    }

    private String renderHtml(final String projectDescription) {
        return this.htmlRenderer.render(this.mdParser.parse(projectDescription));
    }

    private static class LinkAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(final Node node, final String tagNAme, final Map<String, String> attributes) {
            if (node instanceof Link) {
                attributes.put("target", "_blank");
                attributes.put("rel", "noopener");
            }
        }
    }
}