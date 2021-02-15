package com.exasol.releasedroid.formatting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ChecksumFormatterTest {
    @Test
    void testCreateChecksumMap() {
        final String checksum = "76f3e target/testing-release-robot-0.2.0.jar e031e "
                + "target/testing-release-robot-0.2.0-sources.jar 1b37e target/testing-release-robot-0.2.0-javadoc.jar";
        assertThat(ChecksumFormatter.createChecksumMap(checksum),
                equalTo(Map.of("target/testing-release-robot-0.2.0.jar", "76f3e", //
                        "target/testing-release-robot-0.2.0-sources.jar", "e031e", //
                        "target/testing-release-robot-0.2.0-javadoc.jar", "1b37e")));
    }

    @Test
    void testCreateChecksumMapWithNewLineCharacters() {
        final String checksum = "76f3e target/testing-release-robot-0.2.0.jar\n"
                + "e031e  target/testing-release-robot-0.2.0-sources.jar\n"
                + "45eb5  target/testing-release-robot-0.2.0-javadoc.jar\n";
        assertThat(ChecksumFormatter.createChecksumMap(checksum),
                equalTo(Map.of("target/testing-release-robot-0.2.0.jar", "76f3e", //
                        "target/testing-release-robot-0.2.0-sources.jar", "e031e", //
                        "target/testing-release-robot-0.2.0-javadoc.jar", "45eb5")));
    }
}