package com.exasol.releasedroid.adapter.github.progress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProgressMonitorTest {

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

    @Test
    void eta() {
        final Duration estimation = Duration.ofMinutes(1).plusSeconds(1);
        final ProgressMonitor monitor = new ProgressMonitor().withEstimation(estimation).start();
        assertThat(monitor.eta(), equalTo(monitor.getStart().plus(estimation)));
        final Duration elapsed = monitor.elapsed();
        final Duration remaining = monitor.remaining();
        assertThat(secondsAsDouble(elapsed), closeTo(0, 0.5));
        assertThat(secondsAsDouble(remaining), closeTo(61, 0.5));
        assertThat(monitor.elapsed(), greaterThanOrEqualTo(elapsed));
        assertThat(monitor.remaining(), lessThanOrEqualTo(remaining));
    }

    @ParameterizedTest
    @CsvSource(value = { ",false", "1,false", "-1,true" })
    void callback(final Integer secondsBeforeNextCallback, final boolean expected) {
        Duration callbackInterval = null;
        if (secondsBeforeNextCallback != null) {
            callbackInterval = Duration.ofMinutes(secondsBeforeNextCallback);
        }
        final ProgressMonitor monitor = new ProgressMonitor() //
                .withSnoozeInterval(callbackInterval) //
                .start() //
                .snooze();
        assertThat(monitor.requestsInspection(), is(expected));
    }

    @Test
    void initialCallback() {
        final ProgressMonitor monitor = new ProgressMonitor() //
                .withSnoozeInterval(Duration.ofHours(1)) //
                .start();
        assertThat(monitor.requestsInspection(), is(true));
        monitor.snooze();
        assertThat(monitor.requestsInspection(), is(false));
    }

    double secondsAsDouble(final Duration duration) {
        return duration.toSeconds() + (duration.toMillisPart() / 1000.0);
    }

}
