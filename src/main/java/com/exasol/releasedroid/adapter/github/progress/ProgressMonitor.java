package com.exasol.releasedroid.adapter.github.progress;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class ProgressMonitor {

    private Instant start;
    private Optional<Duration> estimation;
    private Optional<Duration> timeout;
    private Optional<Duration> callbackInterval;
    private Instant lastCallback;
    private Instant eta;

    public ProgressMonitor() {
        this.estimation = Optional.empty();
        this.timeout = Optional.empty();
        this.callbackInterval = Optional.empty();
    }

    public ProgressMonitor start() {
        this.start = Instant.now();
        if (this.estimation.isPresent()) {
            this.eta = this.start.plus(this.estimation.get());
        }
        return this;
    }

    public Duration elapsed() {
        return Duration.between(this.start, Instant.now());
    }

    public Duration remaining() {
        return this.estimation.get().minus(elapsed());
    }

    public Instant eta() {
        return this.eta;
    }

    public Optional<Duration> getEstimation() {
        return this.estimation;
    }

    public Instant getStart() {
        return this.start;
    }

    public boolean isTimeout() {
        if (this.timeout.isEmpty()) {
            return false;
        }
        return elapsed().compareTo(this.timeout.get()) > 0;
    }

    public boolean needsCallback() {
        if (this.callbackInterval.isEmpty()) {
            return false;
        }
        if (this.lastCallback == null) {
            return true;
        }
        return this.lastCallback.plus(this.callbackInterval.get()).isBefore(Instant.now());
    }

    public ProgressMonitor notifyCallback() {
        this.lastCallback = Instant.now();
        return this;
    }

    public ProgressMonitor withEstimation(final Duration value) {
        this.estimation = Optional.ofNullable(value);
        return this;
    }

    public ProgressMonitor withTimeout(final Duration value) {
        this.timeout = Optional.ofNullable(value);
        return this;
    }

    public ProgressMonitor withCallbackInterval(final Duration value) {
        this.callbackInterval = Optional.ofNullable(value);
        return this;
    }
}
