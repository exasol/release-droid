package com.exasol.releasedroid.adapter.communityportal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

/**
 * A parser for Community portal template in the JSON format.
 */
public class CommunityPortalTemplateJsonParser {
    private CommunityPortalTemplateJsonParser() {
    }

    /**
     * Parse community portal template.
     *
     * @param json community portal template as a string
     * @return community portal template
     */
    public static CommunityPortalTemplate parse(final String json) {
        final var jsonObject = new JSONObject(json);
        final String projectName = getProjectName(jsonObject);
        final String projectDescription = getProjectDescription(jsonObject);
        final List<String> tags = getTags(jsonObject);
        return new CommunityPortalTemplate(projectName, projectDescription, tags);
    }

    private static String getProjectName(final JSONObject jsonObject) {
        return jsonObject.has("project name") ? jsonObject.getString("project name") : null;
    }

    private static String getProjectDescription(final JSONObject jsonObject) {
        return jsonObject.has("project description") ? jsonObject.getString("project description") : null;
    }

    private static List<String> getTags(final JSONObject jsonObject) {
        final List<String> tags = new ArrayList<>();
        if (jsonObject.has("tags")) {
            final Iterator<Object> tagsArrayIterator = jsonObject.getJSONArray("tags").iterator();
            tagsArrayIterator.forEachRemaining(tag -> tags.add(tag.toString()));
        }
        return tags;
    }
}