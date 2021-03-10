package com.exasol.releasedroid.formatting;

import java.util.*;

/**
 * Formats checksums.
 */
public class ChecksumFormatter {
    private ChecksumFormatter() {
        // intentionally left blank
    }

    /**
     * Create a map with file names and checksums form a string.
     * 
     * @param hashsum hashsum as a string
     * @return map with file names and checksums
     */
    public static Map<String, String> createChecksumMap(final String hashsum) {
        final List<String> list = splitBySpacesAndEmptyLines(hashsum);
        return createMap(list);
    }

    private static List<String> splitBySpacesAndEmptyLines(final String hashsum) {
        final List<String> list = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (final char ch : hashsum.toCharArray()) {
            if (ch == ' ' || ch == '\n' || ch == '\r') {
                if (stringBuilder.length() > 0) {
                    list.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                }
            } else {
                stringBuilder.append(ch);
            }
        }
        if (stringBuilder.length() > 0) {
            list.add(stringBuilder.toString());
        }
        return list;
    }

    private static Map<String, String> createMap(final List<String> list) {
        final Map<String, String> map = new HashMap<>();
        for (int i = 0; i < list.size() - 1; i += 2) {
            map.put(list.get(i + 1), list.get(i));
        }
        return map;
    }
}