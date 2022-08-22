package com.exasol.releasedroid.progress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class ProgressMonitorTest {

    @Test
    void eta() {
        final Duration estimation = Duration.ofMinutes(1).plusSeconds(1);
        final ProgressMonitor monitor = new ProgressMonitor() //
                .withEstimation(new Estimation(Instant.now(), estimation)) //
                .start();
        assertThat(monitor.eta(), equalTo(monitor.getStart().plus(estimation)));
        final Duration elapsed = monitor.elapsed();
        final Duration remaining = monitor.remaining();
        assertThat(secondsAsDouble(elapsed), closeTo(0, 0.5));
        assertThat(secondsAsDouble(remaining), closeTo(61, 0.5));
        assertThat(monitor.elapsed(), greaterThanOrEqualTo(elapsed));
        assertThat(monitor.remaining(), lessThanOrEqualTo(remaining));
    }

    double secondsAsDouble(final Duration duration) {
        return duration.toSeconds() + (duration.toMillisPart() / 1000.0);
    }

}
