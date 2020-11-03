package com.exasol.releaserobot.usecases;

/**
 * Contains common project's constants.
 */
public final class ReleaseRobotConstants {
    public static final String VERSION_REGEX = "(\\d+)\\.(\\d+)\\.(\\d+)";
    public static final String DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private ReleaseRobotConstants() {
        // prevent instantiation
    }
}