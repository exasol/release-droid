package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.formatting.Colorizer.red;
import static com.exasol.releasedroid.formatting.Colorizer.yellow;

import java.util.logging.*;

/**
 * This formatter colors messages of level SEVERE and WARNING to red.
 */
public class LogFormatter extends Formatter {
    private final Formatter formatter = new SimpleFormatter();

    @Override
    public String format(final LogRecord logRecord) {
        final Level level = logRecord.getLevel();
        if (level == Level.SEVERE) {
            logRecord.setMessage(red(logRecord.getMessage()));
        } else if (level == Level.WARNING) {
            logRecord.setMessage(yellow(logRecord.getMessage()));
        }
        return this.formatter.format(logRecord);
    }

}