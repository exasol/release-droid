package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.formatting.Colorizer.red;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class LogFormatterTest {

    @ParameterizedTest
    @MethodSource("redLevels")
    void redColored(final Level level) {
        final LogRecord record = new LogRecord(level, "message");
        new LogFormatter().format(record);
        assertThat(record.getMessage(), equalTo(red("message")));
    }

    @ParameterizedTest
    @MethodSource("normalLevels")
    void normal(final Level level) {
        final LogRecord record = new LogRecord(level, "message");
        new LogFormatter().format(record);
        assertThat(record.getMessage(), equalTo("message"));
    }

    private static Stream<Level> redLevels() {
        return Stream.of(Level.SEVERE, Level.WARNING);
    }

    private static Stream<Level> normalLevels() {
        return Stream.of(Level.FINE, Level.FINER, Level.FINEST, Level.INFO);
    }

}
