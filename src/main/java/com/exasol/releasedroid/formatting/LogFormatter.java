package com.exasol.releasedroid.formatting;

import java.util.logging.*;

/**
 * This formatter colors messages of level SEVERE and WARNING to red.
 */
public class LogFormatter extends Formatter {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private final Formatter formatter = new SimpleFormatter();

    @Override
    public String format(final LogRecord record) {
        if (record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
            record.setMessage(ANSI_RED + record.getMessage() + ANSI_RESET);
        }
        return this.formatter.format(record);
    }
}