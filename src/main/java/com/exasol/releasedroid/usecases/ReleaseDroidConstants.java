package com.exasol.releasedroid.usecases;

/**
 * Contains common project's constants.
 */
public final class ReleaseDroidConstants {
    public static final String VERSION_REGEX = "(\\d+)\\.(\\d+)\\.(\\d+)";
    public static final String DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String RELEASE_DROID_CREDENTIALS = FILE_SEPARATOR + ".release-droid" + FILE_SEPARATOR
            + "credentials";

    private ReleaseDroidConstants() {
        // prevent instantiation
    }
}