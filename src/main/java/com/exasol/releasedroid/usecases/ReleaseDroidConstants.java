package com.exasol.releasedroid.usecases;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contains common project's constants.
 */
public final class ReleaseDroidConstants {
    public static final String VERSION_REGEX = "(\\d+)\\.(\\d+)\\.(\\d+)";
    public static final String DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String HOME_DIRECTORY = System.getProperty("user.home");
    public static final String RELEASE_DROID_DIRECTORY = HOME_DIRECTORY + FILE_SEPARATOR + ".release-droid";
    public static final String RELEASE_DROID_STATE_DIRECTORY = RELEASE_DROID_DIRECTORY + FILE_SEPARATOR + "state";
    public static final String RELEASE_DROID_CREDENTIALS = RELEASE_DROID_DIRECTORY + FILE_SEPARATOR + "credentials";
    public static final String EXASOL_REPOSITORY_OWNER = "exasol";
    public static final String RELEASE_CONFIG_PATH = "release_config.yml";
    public static final String CHANGELOG_FILE_PATH = "doc/changes/changelog.md";
    public static final Path REPORT_PATH = Paths.get(HOME_DIRECTORY, ".release-droid", "last_report.txt");
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private ReleaseDroidConstants() {
        // prevent instantiation
    }
}