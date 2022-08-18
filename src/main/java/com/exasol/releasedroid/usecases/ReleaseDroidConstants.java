package com.exasol.releasedroid.usecases;

/**
 * Contains common project's constants.
 */
public final class ReleaseDroidConstants {
    public static final String VERSION_REGEX = "[v]?(\\d+)\\.(\\d+)\\.(\\d+)";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String HOME_DIRECTORY = System.getProperty("user.home");
    public static final String RELEASE_DROID_DIRECTORY = HOME_DIRECTORY + FILE_SEPARATOR + ".release-droid";
    public static final String RELEASE_CONFIG_PATH = "release_config.yml";
    public static final String RELEASE_DROID_STATE_DIRECTORY = RELEASE_DROID_DIRECTORY + FILE_SEPARATOR + "state";

    private ReleaseDroidConstants() {
        // prevent instantiation
    }
}