package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.formatting.Colorizer.red;

import java.util.logging.*;

/**
 * This formatter colors messages of level SEVERE and WARNING to red.
 */
public class LogFormatter extends Formatter {
    private final Formatter formatter = new SimpleFormatter();

    @Override
    public String format(final LogRecord logRecord) {
        if ((logRecord.getLevel() == Level.SEVERE) || (logRecord.getLevel() == Level.WARNING)) {
            logRecord.setMessage(red(logRecord.getMessage()));
        }
        return this.formatter.format(logRecord);
    }

}