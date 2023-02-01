package com.exasol.releasedroid;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lines {

    private Lines() {
        // only static usage
    }

    public static String lines(final String... string) {
        return Stream.of(string).collect(Collectors.joining(System.lineSeparator()));
    }
}
