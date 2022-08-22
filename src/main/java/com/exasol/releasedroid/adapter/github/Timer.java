package com.exasol.releasedroid.adapter.github;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Keep track of a timeout and provide snooze and remind function.
 */
public class Timer {

    private Instant start;
    private Optional<Duration> timeout;
    private Optional<Duration> snoozeInterval;
    private Instant snoozeStart;

    /**
     * new instance of timer
     */
    public Timer() {
        this.timeout = Optional.empty();
        this.snoozeInterval = Optional.empty();
    }

    /**
     * Start the current timer
     *
     * @return this for fluent programming
     */
    public Timer start() {
        this.start = Instant.now();
        this.snoozeStart = null;
        return this;
    }

    /**
     * @param value duration of timeout
     * @return this for fluent programming
     */
    public Timer withTimeout(final Duration value) {
        this.timeout = Optional.ofNullable(value);
        return this;
    }

    /**
     * @param value duration of snooze interval
     * @return this for fluent programming
     */
    public Timer withSnoozeInterval(final Duration value) {
        this.snoozeInterval = Optional.ofNullable(value);
        this.snoozeStart = null;
        return this;
    }

    /**
     * @return {@code true} if a timeout has been set and the current timer has reached the specified timeout.
     */
    public boolean timeout() {
        if (this.timeout.isEmpty()) {
            return false;
        }
        return elapsed().compareTo(this.timeout.get()) > 0;
    }

    /**
     * @return {@code true} if timer signals an alarm with respect to the snooze interval
     */
    public boolean alarm() {
        if (this.snoozeInterval.isEmpty()) {
            return false;
        }
        if (this.snoozeStart == null) {
            return true;
        }
        return Instant.now().isAfter(this.snoozeStart.plus(this.snoozeInterval.get()));
    }

    /**
     * put the timer to sleep for the duration defined by {@link #withSnoozeInterval}
     *
     * @return this for fluent programming
     */
    public Timer snooze() {
        this.snoozeStart = Instant.now();
        return this;
    }

    /**
     * @return time elapsed time since start of the timer
     */
    public Duration elapsed() {
        return Duration.between(this.start, Instant.now());
    }
}
