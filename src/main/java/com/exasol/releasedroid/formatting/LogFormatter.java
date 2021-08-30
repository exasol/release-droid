package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.ANSI_RESET;

import java.util.logging.*;

/**
 * This formatter colors messages of level SEVERE and WARNING to red.
 */
public class LogFormatter extends Formatter {
    private static final String ANSI_RED = "\u001B[31m";
    private final Formatter formatter = new SimpleFormatter();

    @Override
    public String format(final LogRecord logRecord) {
        if (logRecord.getLevel() == Level.SEVERE || logRecord.getLevel() == Level.WARNING) {
            logRecord.setMessage(ANSI_RED + logRecord.getMessage() + ANSI_RESET);
        }
        return this.formatter.format(logRecord);
    }
}