package com.exasol.releasedroid.adapter;

import static java.util.Collections.emptyList;

import java.util.*;

public class ListExtractor {
    private ListExtractor() {
        // Not instantiable
    }

    public static List<String> extractListOfStrings(final Map<String, Object> parsedConfig, final String key) {
        final Object value = parsedConfig.get(key);
        if (value instanceof Collection) {
            return convertToStringList(value);
        }
        return emptyList();
    }

    private static List<String> convertToStringList(final Object value) {
        final Collection<?> configTags = (Collection<?>) value;
        final List<String> tags = new ArrayList<>();
        for (final Object tag : configTags) {
            if (tag != null) {
                tags.add(tag.toString());
            }
        }
        return tags;
    }
}
