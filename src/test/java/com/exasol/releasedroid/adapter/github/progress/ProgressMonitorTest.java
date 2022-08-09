package com.exasol.releasedroid.adapter.github.progress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.Duration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ProgressMonitorTest {

    @ParameterizedTest
    @CsvSource(value = { ", false", "-1, true", "1, false" })
    void timeout(final Integer timeout, final boolean expected) {
        final ProgressMonitor monitor = new ProgressMonitor();
        if (timeout != null) {
            monitor.withTimeout(Duration.ofHours(timeout));
        }
        monitor.start();
        assertThat(monitor.isTimeout(), is(expected));
    }
}
